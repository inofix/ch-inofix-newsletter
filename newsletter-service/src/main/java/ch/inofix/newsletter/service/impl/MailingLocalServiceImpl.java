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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Validator;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.base.MailingLocalServiceBaseImpl;

/**
 * The implementation of the mailing local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.MailingLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-10 17:21
 * @modified 2017-03-10 00:12
 * @version 1.1.4
 * @see MailingLocalServiceBaseImpl
 * @see ch.inofix.newsletter.service.MailingLocalServiceUtil
 */
@ProviderType
public class MailingLocalServiceImpl extends MailingLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.newsletter.service.MailingLocalServiceUtil} to access the
     * mailing local service.
     */
    @Override
    public Mailing addMailing(long userId, long groupId, String title, String template, long newsletterId,
            String articleId, long articleGroupId, Date publishDate, Date sendDate, ServiceContext serviceContext)
            throws PortalException  {

        boolean sent = false;

        Mailing mailing = saveMailing(userId, groupId, 0, title, template, newsletterId, articleId, articleGroupId,
                publishDate, sendDate, sent, serviceContext);

        // Asset

        resourceLocalService.addResources(mailing.getCompanyId(), groupId, userId, Mailing.class.getName(),
                mailing.getMailingId(), false, true, true);

        updateAsset(userId, mailing, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds());

        return mailing;
    }

    @Override
    public void checkMailings() throws PortalException  {

        if (_log.isDebugEnabled()) {
            _log.debug("checkMailings()");
        }

        // Get the list of unsent mailings.

        List<Mailing> mailings = mailingPersistence.findBySent(false);

        Date now = new Date();

        for (Mailing mailing : mailings) {

            if (mailing.getSendDate() != null) {

                Company company = CompanyLocalServiceUtil.getCompany(mailing.getCompanyId());

                int offset = company.getTimeZone().getOffset(now.getTime());

                Date compareDate = new Date(now.getTime() + offset);

                if (compareDate.after(mailing.getSendDate())) {

                    _log.info("Sending mailing because " + compareDate + " is after " + mailing.getSendDate());

                    Map<String, String[]> parameterMap = new HashMap<String, String[]>();
                    parameterMap.put("mailingId", new String[] { String.valueOf(mailing.getMailingId()) });

                    sendMailings(mailing.getGroupId(), parameterMap, now);

                } else {

                    _log.info("Not sending mailing because " + compareDate + " is before " + mailing.getSendDate());
                }
            }
        }
    }

    @Override
    public Mailing deleteMailing(long mailingId) throws PortalException  {

        Mailing mailing = mailingPersistence.remove(mailingId);

        resourceLocalService.deleteResource(mailing.getCompanyId(), Mailing.class.getName(),
                ResourceConstants.SCOPE_INDIVIDUAL, mailingId);

        // Asset

        Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(Mailing.class);
        indexer.delete(mailing);

        AssetEntry assetEntry = assetEntryLocalService.fetchEntry(Mailing.class.getName(), mailingId);

        assetEntryLocalService.deleteEntry(assetEntry);

        return mailing;
    }

    private Mailing saveMailing(long userId, long groupId, long mailingId, String title, String template,
            long newsletterId, String articleId, long articleGroupId, Date publishDate, Date sendDate, boolean sent,
            ServiceContext serviceContext) throws PortalException  {

        User user = userPersistence.findByPrimaryKey(userId);
        Date now = new Date();
        Mailing mailing = null;

        if (mailingId > 0) {
            mailing = mailingLocalService.getMailing(mailingId);
        } else {
            mailingId = counterLocalService.increment();
            mailing = mailingPersistence.create(mailingId);
            mailing.setCompanyId(user.getCompanyId());
            mailing.setGroupId(groupId);
            mailing.setUserId(user.getUserId());
            mailing.setUserName(user.getFullName());
            mailing.setCreateDate(now);
        }

        mailing.setModifiedDate(now);

        mailing.setTitle(title);
        mailing.setTemplate(template);
        mailing.setNewsletterId(newsletterId);
        mailing.setArticleId(articleId);
        mailing.setArticleGroupId(articleGroupId);
        mailing.setPublishDate(publishDate);
        mailing.setSendDate(sendDate);
        mailing.setSent(sent);
        mailing.setExpandoBridgeAttributes(serviceContext);

        mailing = mailingPersistence.update(mailing);

        return mailing;

    }

    private void sendEmail(Mailing mailing, Subscriber subscriber) throws PortalException  {

        Newsletter newsletter = null;

        if (mailing.getNewsletterId() > 0) {
            newsletter = newsletterLocalService.getNewsletter(mailing.getNewsletterId());
        }

        long companyId = 0;

        String fromAddress = null;
        String fromName = null;

        if (newsletter != null) {
            companyId = newsletter.getCompanyId();
            fromAddress = newsletter.getFromAddress();
            fromName = newsletter.getFromName();
        }

        String toAddress = subscriber.getEmail();
        String toName = "TODO: implement toName";
        // TODO
//        String toName = subscriber.getName();

        if (Validator.isEmailAddress(toAddress)) {

            String subject = mailing.getTitle();

            Map<String, Object> contextObjects = new HashMap<String, Object>();
            contextObjects.put("subscriber", subscriber);

            String body = "";

            // TODO
            body = "TODO: implement body setup";
//            body = mailingService.prepareMailing(contextObjects, mailing.getMailingId());

            SubscriptionSender subscriptionSender = new SubscriptionSender();

            subscriptionSender.setSubject(subject);
            subscriptionSender.setCompanyId(companyId);
            subscriptionSender.setBody(body);
            subscriptionSender.setFrom(fromAddress, fromName);
            subscriptionSender.setHtmlFormat(true);
            subscriptionSender.setMailId("newsletter", mailing.getMailingId());
            subscriptionSender.setPortletId(PortletKeys.NEWSLETTER_MANAGER);
            subscriptionSender.setScopeGroupId(mailing.getGroupId());
            subscriptionSender.addRuntimeSubscribers(toAddress, toName);

            subscriptionSender.flushNotificationsAsync();

        } else {
            _log.warn("The to-address " + toAddress + " is not valid");
        }
    }

    @Override
    public void sendMailings(long groupId, Map<String, String[]> parameterMap, Date sendDate)
            throws PortalException  {

        if (sendDate == null) {
            sendDate = new Date();
        }

        long mailingId = GetterUtil.getLong(ArrayUtil.getValue(parameterMap.get("mailingId"), 0));

        String email = GetterUtil.getString(ArrayUtil.getValue(parameterMap.get("email"), 0));

        Mailing mailing = null;

        if (mailingId > 0) {
            mailing = mailingLocalService.getMailing(mailingId);
        }

        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        if (Validator.isNotNull(email)) {

            // test mail

            Subscriber subscriber = subscriberLocalService.createSubscriber(0);
            subscriber.setEmail(email);
            subscribers.add(subscriber);

        } else if (mailing != null) {

            Newsletter newsletter = null;

            if (mailing.getNewsletterId() > 0) {
                newsletter = newsletterLocalService.getNewsletter(mailing.getNewsletterId());
            }

            // TODO
//            if (newsletter != null) {
//                subscribers = subscriberService.getSubscribersFromVCardGroup(newsletter.getCompanyId(), groupId,
//                        newsletter.getVCardGroupId(), 0, Integer.MAX_VALUE);
//            }
        }

        int numSent = 0;
        int numFailed = 0;

        for (Subscriber subscriber : subscribers) {

            try {
                sendEmail(mailing, subscriber);
                numSent++;
            } catch (Exception e) {
                _log.error("Send mailing failed for subscriber " + subscriber.getEmail());
                _log.error(e);
                numFailed++;
            }
        }

        _log.info("Sent " + numSent + " mailings");
        _log.info("Sending failed for " + numFailed + " mailings");

        // Mark mailing sent if it wasn't sent for testing

        if (Validator.isNull(email)) {

            boolean sent = true;

            ServiceContext serviceContext = new ServiceContext();
            serviceContext.setCompanyId(mailing.getCompanyId());
            serviceContext.setScopeGroupId(mailing.getGroupId());
            serviceContext.setModifiedDate(sendDate);

            updateMailing(mailing.getUserId(), mailing.getGroupId(), mailing.getMailingId(), mailing.getTitle(),
                    mailing.getTemplate(), mailing.getNewsletterId(), mailing.getArticleId(),
                    mailing.getArticleGroupId(), mailing.getPublishDate(), sendDate, sent, serviceContext);
        }
    }

    // TODO
//    @Override
//    public long sendMailingsInBackground(long userId, String taskName, long groupId, Map<String, String[]> parameterMap)
//            throws PortalException  {
//
//        Map<String, Serializable> taskContextMap = new HashMap<String, Serializable>();
//        taskContextMap.put("userId", userId);
//        taskContextMap.put("groupId", groupId);
//        taskContextMap.put("parameterMap", (Serializable) parameterMap);
//
//        String[] servletContextNames = parameterMap.get("servletContextNames");
//
//        BackgroundTask backgroundTask = BackgroundTaskLocalServiceUtil.addBackgroundTask(userId, groupId, taskName,
//                servletContextNames, MailingBackgroundTaskExecutor.class, taskContextMap, new ServiceContext());
//
//        return backgroundTask.getBackgroundTaskId();
//
//    }

    @Override
    public void updateAsset(long userId, Mailing mailing, long[] assetCategoryIds, String[] assetTagNames,
            long[] assetLinkEntryIds) throws PortalException  {

        boolean visible = true;

        // TODO: What's the classTypeId?
        long classTypeId = 0;
        Date startDate = null;
        Date endDate = null;
        Date expirationDate = null;
        String mimeType = "text/plain";
        String title = mailing.getTitle();
        String description = "";
        String summary = "";
        // TODO: What does url mean in this context?
        String url = null;
        // TODO: What does layoutUuid mean in this context?
        String layoutUuid = null;
        int height = 0;
        int width = 0;
        Integer priority = null;
        boolean sync = false;

        assetEntryLocalService.updateEntry(userId, mailing.getGroupId(), mailing.getCreateDate(),
                mailing.getModifiedDate(), Mailing.class.getName(), mailing.getMailingId(), mailing.getUuid(),
                classTypeId, assetCategoryIds, assetTagNames, visible, startDate, endDate, expirationDate, mimeType,
                title, description, summary, url, layoutUuid, height, width, priority, sync);

        Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(Mailing.class);
        indexer.reindex(mailing);
    }

    @Override
    public Mailing updateMailing(long userId, long groupId, long mailingId, String title, String template,
            long newsletterId, String articleId, long articleGroupId, Date publishDate, Date sendDate, boolean sent,
            ServiceContext serviceContext) throws PortalException  {

        Mailing mailing = saveMailing(userId, groupId, mailingId, title, template, newsletterId, articleId,
                articleGroupId, publishDate, sendDate, sent, serviceContext);

        // Asset

        resourceLocalService.updateResources(serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
                mailing.getTitle(), mailingId, serviceContext.getGroupPermissions(),
                serviceContext.getGuestPermissions());

        updateAsset(userId, mailing, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(),
                serviceContext.getAssetLinkEntryIds());

        return mailing;
    }

    private static Log _log = LogFactoryUtil.getLog(MailingLocalServiceImpl.class.getName());

}