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

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;

import aQute.bnd.annotation.ProviderType;
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
 * @modified 2017-03-22 10:41
 * @version 1.0.1
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
    @Indexable(type = IndexableType.REINDEX)
    public Subscriber reIndexBibligraphy(long subscriberId) throws PortalException {

        return getSubscriber(subscriberId);

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

    private static final Log _log = LogFactoryUtil.getLog(SubscriberLocalServiceImpl.class);
}