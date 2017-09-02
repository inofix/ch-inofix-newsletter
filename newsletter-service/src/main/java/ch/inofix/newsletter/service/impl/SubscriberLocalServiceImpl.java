/**
 * Copyright (c) 2000-present Inofix GmbH, Luzern. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package ch.inofix.newsletter.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.base.SubscriberLocalServiceBaseImpl;
import ch.inofix.newsletter.social.SubscriberActivityKeys;

/**
 * The implementation of the newsletter local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.SubscriberLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-08 16:41
 * @modified 2017-09-02 11:08
 * @version 1.0.2
 * @see SubscriberLocalServiceBaseImpl
 * @see ch.inofix.newsletter.service.SubscriberLocalServiceUtil
 */
@ProviderType
public class SubscriberLocalServiceImpl extends SubscriberLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.newsletter.service.SubscriberLocalServiceUtil} to access the
     * newsletter local service.
     */

    @Indexable(type = IndexableType.REINDEX)
    @Override
    public Subscriber addSubscriber(long userId, String title, String template, String fromAddress, String fromName,
            boolean useHttps, ServiceContext serviceContext) throws PortalException {

        // Subscriber

        User user = userPersistence.findByPrimaryKey(userId);
        long groupId = serviceContext.getScopeGroupId();

        long subscriberId = counterLocalService.increment();

        Subscriber subscriber = subscriberPersistence.create(subscriberId);

        subscriber.setUuid(serviceContext.getUuid());
        subscriber.setGroupId(groupId);
        subscriber.setCompanyId(user.getCompanyId());
        subscriber.setUserId(user.getUserId());
        subscriber.setUserName(user.getFullName());
        subscriber.setExpandoBridgeAttributes(serviceContext);

        // TODO
        subscriber.setTitle(title);
        // subscriber.setTemplate(template);
        // subscriber.setFromAddress(fromAddress);
        // subscriber.setFromName(fromName);
        // subscriber.setUseHttps(useHttps);
        subscriber.setExpandoBridgeAttributes(serviceContext);

        subscriberPersistence.update(subscriber);

        // Resources

        if (serviceContext.isAddGroupPermissions() || serviceContext.isAddGuestPermissions()) {

            addSubscriberResources(subscriber, serviceContext.isAddGroupPermissions(),
                    serviceContext.isAddGuestPermissions());
        } else {
            addSubscriberResources(subscriber, serviceContext.getModelPermissions());
        }

        // Asset

        updateAsset(userId, subscriber, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        // Social

        JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

        extraDataJSONObject.put("title", subscriber.getTitle());

        socialActivityLocalService.addActivity(userId, groupId, Subscriber.class.getName(), subscriberId,
                SubscriberActivityKeys.ADD_SUBSCRIBER, extraDataJSONObject.toString(), 0);

        return subscriber;

    }

    @Override
    public void addSubscriberResources(Subscriber subscriber, boolean addGroupPermissions, boolean addGuestPermissions)
            throws PortalException {

        resourceLocalService.addResources(subscriber.getCompanyId(), subscriber.getGroupId(), subscriber.getUserId(),
                Subscriber.class.getName(), subscriber.getSubscriberId(), false, addGroupPermissions,
                addGuestPermissions);
    }

    @Override
    public void addSubscriberResources(Subscriber subscriber, ModelPermissions modelPermissions)
            throws PortalException {

        resourceLocalService.addModelResources(subscriber.getCompanyId(), subscriber.getGroupId(),
                subscriber.getUserId(), Subscriber.class.getName(), subscriber.getSubscriberId(), modelPermissions);
    }

    @Override
    public void addSubscriberResources(long subscriberId, boolean addGroupPermissions, boolean addGuestPermissions)
            throws PortalException {

        Subscriber subscriber = subscriberPersistence.findByPrimaryKey(subscriberId);

        addSubscriberResources(subscriber, addGroupPermissions, addGuestPermissions);
    }

    @Override
    public void addSubscriberResources(long subscriberId, ModelPermissions modelPermissions) throws PortalException {

        Subscriber subscriber = subscriberPersistence.findByPrimaryKey(subscriberId);

        addSubscriberResources(subscriber, modelPermissions);
    }

    @Indexable(type = IndexableType.DELETE)
    @Override
    @SystemEvent(type = SystemEventConstants.TYPE_DELETE)
    public Subscriber deleteSubscriber(Subscriber subscriber) throws PortalException {

        // Subscriber

        subscriberPersistence.remove(subscriber);

        // Resources

        resourceLocalService.deleteResource(subscriber.getCompanyId(), Subscriber.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, subscriber.getSubscriberId());

        // Subscriptions

        subscriptionLocalService.deleteSubscriptions(subscriber.getCompanyId(), Subscriber.class.getName(),
                subscriber.getSubscriberId());

        // Asset

        assetEntryLocalService.deleteEntry(Subscriber.class.getName(), subscriber.getSubscriberId());

        // Expando

        expandoRowLocalService.deleteRows(subscriber.getSubscriberId());

        // Ratings

        ratingsStatsLocalService.deleteStats(Subscriber.class.getName(), subscriber.getSubscriberId());

        // Trash

        trashEntryLocalService.deleteEntry(Subscriber.class.getName(), subscriber.getSubscriberId());

        // Workflow

        // TODO: do we need workflow support?
        // workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
        // subscriber.getCompanyId(), subscriber.getGroupId(),
        // Subscriber.class.getName(), subscriber.getSubscriberId());

        return subscriber;
    }

    @Override
    public Subscriber deleteSubscriber(long subscriberId) throws PortalException {
        Subscriber subscriber = subscriberPersistence.findByPrimaryKey(subscriberId);

        return subscriberLocalService.deleteSubscriber(subscriber);
    }

    @Override
    public Subscriber getSubscriber(long subscriberId) throws PortalException {
        return subscriberPersistence.findByPrimaryKey(subscriberId);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        if (sort == null) {
            sort = new Sort(Field.MODIFIED_DATE, true);
        }

        String description = null;
        String workPackage = null;
        boolean andOperator = false;

        if (Validator.isNotNull(keywords)) {

            description = keywords;
            workPackage = keywords;

        } else {
            andOperator = true;
        }

        return search(userId, groupId, ownerUserId, workPackage, description, WorkflowConstants.STATUS_ANY, null,
                andOperator, start, end, sort);

    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String title, String description, int status,
            LinkedHashMap<String, Object> params, boolean andSearch, int start, int end, Sort sort)
            throws PortalException {

        if (sort == null) {
            sort = new Sort(Field.MODIFIED_DATE, true);
        }

        Indexer<Mailing> indexer = IndexerRegistryUtil.getIndexer(Mailing.class.getName());

        SearchContext searchContext = buildSearchContext(userId, groupId, ownerUserId, title, description, status,
                params, andSearch, start, end, sort);

        return indexer.search(searchContext);

    }

    @Override
    public void updateAsset(long userId, Subscriber subscriber, long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds, Double priority) throws PortalException {

        boolean visible = false;

        Date publishDate = null;

        // TODO
        // if (subscriber.isApproved()) {
        // visible = true;
        // publishDate = subscriber.getCreateDate();
        // }

        String summary = HtmlUtil.extractText(StringUtil.shorten(subscriber.getTitle(), 500));

        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, subscriber.getGroupId(),
                subscriber.getCreateDate(), subscriber.getModifiedDate(), Subscriber.class.getName(),
                subscriber.getSubscriberId(), subscriber.getUuid(), 0, assetCategoryIds, assetTagNames, true, visible,
                null, null, publishDate, null, ContentTypes.TEXT_HTML, subscriber.getTitle(), subscriber.getTitle(),
                summary, null, null, 0, 0, priority);

        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(), assetLinkEntryIds,
                AssetLinkConstants.TYPE_RELATED);
    }

    @Override
    @Indexable(type = IndexableType.REINDEX)
    public Subscriber updateSubscriber(long userId, long subscriberId, String title, String template,
            String fromAddress, String fromName, boolean useHttps, ServiceContext serviceContext)
            throws PortalException {

        // Subscriber

        User user = userPersistence.findByPrimaryKey(userId);

        Subscriber subscriber = subscriberPersistence.findByPrimaryKey(subscriberId);

        long groupId = serviceContext.getScopeGroupId();

        subscriber.setUuid(serviceContext.getUuid());
        subscriber.setGroupId(groupId);
        subscriber.setCompanyId(user.getCompanyId());
        subscriber.setUserId(user.getUserId());
        subscriber.setUserName(user.getFullName());
        subscriber.setExpandoBridgeAttributes(serviceContext);

        // TODO
        subscriber.setTitle(title);
        // subscriber.setTemplate(template);
        // subscriber.setFromAddress(fromAddress);
        // subscriber.setFromName(fromName);
        // subscriber.setUseHttps(useHttps);
        subscriber.setExpandoBridgeAttributes(serviceContext);

        subscriberPersistence.update(subscriber);

        // Resources

        resourceLocalService.addModelResources(subscriber, serviceContext);

        // Asset

        updateAsset(userId, subscriber, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        // Social

        JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

        extraDataJSONObject.put("title", subscriber.getTitle());

        socialActivityLocalService.addActivity(userId, groupId, Subscriber.class.getName(), subscriberId,
                SubscriberActivityKeys.UPDATE_SUBSCRIBER, extraDataJSONObject.toString(), 0);

        return subscriber;

    }

    @Override
    public void updateSubscriberResources(Subscriber subscriber, ModelPermissions modelPermissions)
            throws PortalException {

        resourceLocalService.updateResources(subscriber.getCompanyId(), subscriber.getGroupId(),
                Subscriber.class.getName(), subscriber.getSubscriberId(), modelPermissions);
    }

    @Override
    public void updateSubscriberResources(Subscriber subscriber, String[] groupPermissions, String[] guestPermissions)
            throws PortalException {

        resourceLocalService.updateResources(subscriber.getCompanyId(), subscriber.getGroupId(),
                Subscriber.class.getName(), subscriber.getSubscriberId(), groupPermissions, guestPermissions);
    }
    
    protected SearchContext buildSearchContext(long userId, long groupId, long ownerUserId, String title,
            String description, int status, LinkedHashMap<String, Object> params, boolean andSearch, int start, int end,
            Sort sort) throws PortalException {

        SearchContext searchContext = new SearchContext();

        searchContext.setAttribute(Field.STATUS, status);

        if (Validator.isNotNull(description)) {
            searchContext.setAttribute("description", description);
        }

        if (Validator.isNotNull(title)) {
            searchContext.setAttribute("title", title);
        }

        searchContext.setAttribute("paginationType", "more");

        Group group = GroupLocalServiceUtil.getGroup(groupId);

        searchContext.setCompanyId(group.getCompanyId());

        if (ownerUserId > 0) {
            searchContext.setOwnerUserId(ownerUserId);
        }

        searchContext.setEnd(end);
        if (groupId > 0) {
            searchContext.setGroupIds(new long[] { groupId });
        }
        searchContext.setSorts(sort);
        searchContext.setStart(start);
        searchContext.setUserId(userId);

        searchContext.setAndSearch(andSearch);

        if (params != null) {

            String keywords = (String) params.remove("keywords");

            if (Validator.isNotNull(keywords)) {
                searchContext.setKeywords(keywords);
            }
        }

        QueryConfig queryConfig = new QueryConfig();

        queryConfig.setHighlightEnabled(false);
        queryConfig.setScoreEnabled(false);

        searchContext.setQueryConfig(queryConfig);

        if (sort != null) {
            searchContext.setSorts(sort);
        }

        searchContext.setStart(start);

        return searchContext;
    }

    private static final Log _log = LogFactoryUtil.getLog(SubscriberLocalServiceImpl.class);
}