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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Newsletter;
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
 * @modified 2017-09-14 10:43
 * @version 1.0.9
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

        // Newsletter

        User user = userPersistence.findByPrimaryKey(userId);
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

        resourceLocalService.addResources(newsletter.getCompanyId(), groupId, userId, Mailing.class.getName(),
                newsletter.getNewsletterId(), false, true, true);

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

        // Newsletter

        newsletterPersistence.remove(newsletter);

        // Resources

        resourceLocalService.deleteResource(newsletter.getCompanyId(), Newsletter.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, newsletter.getNewsletterId());

        // Subscriptions

        subscriptionLocalService.deleteSubscriptions(newsletter.getCompanyId(), Newsletter.class.getName(),
                newsletter.getNewsletterId());

        // Asset

        assetEntryLocalService.deleteEntry(Newsletter.class.getName(), newsletter.getNewsletterId());

        // Expando

        expandoRowLocalService.deleteRows(newsletter.getNewsletterId());

        // Ratings

        ratingsStatsLocalService.deleteStats(Newsletter.class.getName(), newsletter.getNewsletterId());

        // Trash

        trashEntryLocalService.deleteEntry(Newsletter.class.getName(), newsletter.getNewsletterId());

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

    public void updateAsset(long userId, Newsletter newsletter, long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds, Double priority) throws PortalException {

        boolean visible = false;

        Date publishDate = null;

        // TODO
        // if (newsletter.isApproved()) {
        // visible = true;
        // publishDate = newsletter.getCreateDate();
        // }

        String summary = HtmlUtil.extractText(StringUtil.shorten(newsletter.getTitle(), 500));

        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, newsletter.getGroupId(),
                newsletter.getCreateDate(), newsletter.getModifiedDate(), Newsletter.class.getName(),
                newsletter.getNewsletterId(), newsletter.getUuid(), 0, assetCategoryIds, assetTagNames, true, visible,
                null, null, publishDate, null, ContentTypes.TEXT_HTML, newsletter.getTitle(), newsletter.getTitle(),
                summary, null, null, 0, 0, priority);

        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(), assetLinkEntryIds,
                AssetLinkConstants.TYPE_RELATED);
    }

    @Indexable(type = IndexableType.REINDEX)
    public Newsletter updateNewsletter(long userId, long newsletterId, String title, String template,
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

    private static final Log _log = LogFactoryUtil.getLog(NewsletterLocalServiceImpl.class);
}