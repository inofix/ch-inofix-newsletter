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

import java.util.LinkedHashMap;
import java.util.List;

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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.exception.NewsletterReferencedByMailingException;
import ch.inofix.newsletter.exception.NewsletterReferencedBySubscriberException;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.base.NewsletterLocalServiceBaseImpl;
import ch.inofix.newsletter.social.NewsletterActivityKeys;

/**
 * The implementation of the newsletter local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.NewsletterLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-08 16:41
 * @modified 2018-01-29 13:40
 * @version 1.1.3
 * @see NewsletterLocalServiceBaseImpl
 * @see ch.inofix.newsletter.service.NewsletterLocalServiceUtil
 */
@ProviderType
public class NewsletterLocalServiceImpl extends NewsletterLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.newsletter.service.NewsletterLocalServiceUtil} to access the
     * newsletter local service.
     */

    @Override
    @Indexable(type = IndexableType.REINDEX)
    public Newsletter addNewsletter(long userId, String title, String template, String fromAddress, String fromName,
            boolean useHttps, ServiceContext serviceContext) throws PortalException {
        
        _log.info("addNewsletter()");
                        
        // Newsletter

        User user = userLocalService.getUser(userId);
        long groupId = serviceContext.getScopeGroupId();
        long newsletterId = counterLocalService.increment();

        Newsletter newsletter = newsletterPersistence.create(newsletterId);

        newsletter.setUuid(serviceContext.getUuid());
        newsletter.setGroupId(groupId);
        newsletter.setCompanyId(user.getCompanyId());
        newsletter.setUserId(user.getUserId());
        newsletter.setUserName(user.getFullName());
        newsletter.setExpandoBridgeAttributes(serviceContext);

        // TODO: validate the template string
        newsletter.setTitle(title);
        newsletter.setTemplate(template);
        newsletter.setFromAddress(fromAddress);
        newsletter.setFromName(fromName);
        newsletter.setUseHttps(useHttps);
        newsletter.setExpandoBridgeAttributes(serviceContext);

        newsletterPersistence.update(newsletter);

        // Resources

        resourceLocalService.addModelResources(newsletter, serviceContext);

        // Asset

        updateAsset(userId, newsletter, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        // Social

        JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

        extraDataJSONObject.put("title", newsletter.getTitle());

        socialActivityLocalService.addActivity(userId, groupId, Newsletter.class.getName(), newsletterId,
                NewsletterActivityKeys.ADD_NEWSLETTER, extraDataJSONObject.toString(), 0);

        return newsletter;

    }

    @Indexable(type = IndexableType.DELETE)
    @Override
    @SystemEvent(type = SystemEventConstants.TYPE_DELETE)
    public Newsletter deleteNewsletter(Newsletter newsletter) throws PortalException {

        // Do no delete if it's referenced by one or more mailings.

        List<Mailing> mailings = mailingPersistence.findByNewsletterId(newsletter.getNewsletterId());

        if (mailings.size() > 0) {
            throw new NewsletterReferencedByMailingException();
        }
        
        // Do no delete if it's referenced by one or more subscribers.

        List<Subscriber> subscribers = subscriberPersistence.findByNewsletterId(newsletter.getNewsletterId());

        if (subscribers.size() > 0) {
            throw new NewsletterReferencedBySubscriberException();
        }

        // Newsletter

        newsletterPersistence.remove(newsletter);

        // Resources

        resourceLocalService.deleteResource(newsletter.getCompanyId(), Newsletter.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, newsletter.getNewsletterId());

        // Asset

        assetEntryLocalService.deleteEntry(Newsletter.class.getName(), newsletter.getNewsletterId());

        // Workflow

        // TODO: do we need workflow support?
        // workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
        // newsletter.getCompanyId(), newsletter.getGroupId(),
        // Newsletter.class.getName(), newsletter.getNewsletterId());

        return newsletter;
    }

    @Override
    public Newsletter deleteNewsletter(long newsletterId) throws PortalException {
        Newsletter newsletter = newsletterPersistence.findByPrimaryKey(newsletterId);

        return newsletterLocalService.deleteNewsletter(newsletter);
    }

    @Override
    public Newsletter getNewsletter(long newsletterId) throws PortalException {
        return newsletterPersistence.findByPrimaryKey(newsletterId);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {
        
        if (sort == null) {
            sort = new Sort(Field.MODIFIED_DATE, true);
        }

        String title = null;
        String fromAddress = null;
        String fromName = null;
        boolean andOperator = false;

        if (Validator.isNotNull(keywords)) {

            title = keywords;
            fromAddress = keywords;
            fromName = keywords;

        } else {
            andOperator = true;
        }

        return search(userId, groupId, ownerUserId, title, fromAddress, fromName, WorkflowConstants.STATUS_ANY, null,
                andOperator, start, end, sort);

    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String title, String fromAddress, String fromName, int status,
            LinkedHashMap<String, Object> params, boolean andSearch, int start, int end, Sort sort)
            throws PortalException {
        
        if (sort == null) {
            sort = new Sort(Field.MODIFIED_DATE, true);
        }

        Indexer<Mailing> indexer = IndexerRegistryUtil.getIndexer(Newsletter.class.getName());

        SearchContext searchContext = buildSearchContext(userId, groupId, ownerUserId, title, fromAddress, fromName, status,
                params, andSearch, start, end, sort);

        return indexer.search(searchContext);

    }

    @Override
    public void updateAsset(long userId, Newsletter newsletter,
            long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds, Double priority) throws PortalException {

        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId,
                newsletter.getGroupId(), newsletter.getCreateDate(),
                newsletter.getModifiedDate(), Newsletter.class.getName(),
                newsletter.getNewsletterId(), newsletter.getUuid(), 0,
                assetCategoryIds, assetTagNames, true, true, null, null,
                newsletter.getCreateDate(), null, ContentTypes.TEXT_PLAIN,
                newsletter.getTitle(), newsletter.getTitle(), null, null, null,
                0, 0, priority);

        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(),
                assetLinkEntryIds, AssetLinkConstants.TYPE_RELATED);

    }

    @Indexable(type = IndexableType.REINDEX)
    public Newsletter updateNewsletter(long newsletterId, long userId, String title, String template,
            String fromAddress, String fromName, boolean useHttps, ServiceContext serviceContext)
            throws PortalException {
        
        // Newsletter

        User user = userPersistence.findByPrimaryKey(userId);

        Newsletter newsletter = newsletterPersistence.findByPrimaryKey(newsletterId);

        long groupId = serviceContext.getScopeGroupId();

        newsletter.setUuid(serviceContext.getUuid());
        newsletter.setGroupId(groupId);
        newsletter.setCompanyId(user.getCompanyId());
        newsletter.setUserId(user.getUserId());
        newsletter.setUserName(user.getFullName());
        newsletter.setExpandoBridgeAttributes(serviceContext);

        // TODO: validate the template string
        newsletter.setTitle(title);
        newsletter.setTemplate(template);
        newsletter.setFromAddress(fromAddress);
        newsletter.setFromName(fromName);
        newsletter.setUseHttps(useHttps);
        newsletter.setExpandoBridgeAttributes(serviceContext);

        newsletterPersistence.update(newsletter);

        // Resources

        resourceLocalService.addModelResources(newsletter, serviceContext);

        // Asset

        updateAsset(userId, newsletter, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds(), serviceContext.getAssetPriority());

        // Social

        JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

        extraDataJSONObject.put("title", newsletter.getTitle());

        socialActivityLocalService.addActivity(userId, groupId, Newsletter.class.getName(), newsletterId,
                NewsletterActivityKeys.UPDATE_NEWSLETTER, extraDataJSONObject.toString(), 0);

        return newsletter;

    }

    protected SearchContext buildSearchContext(long userId, long groupId, long ownerUserId, String title,
            String fromAddress, String fromName, int status, LinkedHashMap<String, Object> params, boolean andSearch, int start, int end,
            Sort sort) throws PortalException {

        SearchContext searchContext = new SearchContext();

        searchContext.setAttribute(Field.STATUS, status);

        if (Validator.isNotNull(fromAddress)) {
            searchContext.setAttribute("fromAddress", fromAddress);
        }

        if (Validator.isNotNull(fromName)) {
            searchContext.setAttribute("fromName", fromName);
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

    private static final Log _log = LogFactoryUtil.getLog(NewsletterLocalServiceImpl.class);
}