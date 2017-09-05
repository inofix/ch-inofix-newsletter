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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.security.ActionKeys;
import ch.inofix.newsletter.service.base.MailingServiceBaseImpl;
import ch.inofix.newsletter.service.permission.MailingPermission;
import ch.inofix.newsletter.service.permission.NewsletterPortletPermission;

/**
 * The implementation of the mailing remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.MailingService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-10 17:19
 * @modified 2017-09-02 11:07
 * @version 1.1.5
 * @see MailingServiceBaseImpl
 * @see ch.inofix.newsletter.service.MailingServiceUtil
 */
@ProviderType
public class MailingServiceImpl extends MailingServiceBaseImpl {

    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.newsletter.service.MailingServiceUtil} to access the mailing
     * remote service.
     */

    @Override
    public Mailing addMailing(long userId, long groupId, String title, String template, long newsletterId,
            String articleId, long articleGroupId, Date publishDate, Date sendDate, ServiceContext serviceContext)
            throws PortalException {

        NewsletterPortletPermission.check(getPermissionChecker(), groupId, ActionKeys.ADD_NEWSLETTER);

        return mailingLocalService.addMailing(userId, groupId, title, template, newsletterId, articleId, articleGroupId,
                publishDate, sendDate, serviceContext);

    }

    @Override
    public Mailing createMailing() throws PortalException {

        // Create an empty mailing - no permission check required
        return mailingLocalService.createMailing(0);

    }

    @Override
    public Mailing deleteMailing(long mailingId) throws PortalException {

        MailingPermission.check(getPermissionChecker(), mailingId, ActionKeys.DELETE);

        Mailing mailing = mailingLocalService.deleteMailing(mailingId);

        return mailing;

    }

    @Override
    public Mailing getMailing(long mailingId) throws PortalException {

        MailingPermission.check(getPermissionChecker(), mailingId, ActionKeys.VIEW);

        return mailingLocalService.getMailing(mailingId);

    }

    // TODO: move this method to separate utility class and perform mailing,
    // article and newsletter lookup before.

    // @Override
    // public String prepareMailing(Map<String, Object> contextObjects,
    // long mailingId) throws PortalException {
    //
    // JournalArticle article = null;
    // Newsletter newsletter = null;
    //
    // String protocol = "http://";
    // String script = null;
    //
    // if (mailingId > 0) {
    //
    // Mailing mailing = mailingLocalService.getMailing(mailingId);
    //
    // contextObjects.put("mailing", mailing);
    //
    // long articleGroupId = mailing.getArticleGroupId();
    //
    // _log.info("articleGroupId = " + articleGroupId);
    //
    // if (articleGroupId == 0) {
    // articleGroupId = mailing.getGroupId();
    // }
    //
    // _log.info("articleGroupId = " + articleGroupId);
    //
    // long newsletterId = mailing.getNewsletterId();
    //
    // if (newsletterId > 0) {
    //
    // // TODO: resolve permission handling
    // newsletter = newsletterLocalService.getNewsletter(newsletterId);
    // script = newsletter.getTemplate();
    // if (newsletter.isUseHttps()) {
    // protocol = "https://";
    // }
    // }
    //
    // // mailing.template overrides newsletter.template
    //
    // if (Validator.isNotNull(mailing.getTemplate())) {
    // script = mailing.getTemplate();
    // }
    //
    // String articleId = mailing.getArticleId();
    //
    // if (Validator.isNotNull(articleId)) {
    //
    // article = JournalArticleLocalServiceUtil.getLatestArticle(
    // articleGroupId, articleId,
    // WorkflowConstants.STATUS_APPROVED);
    // }
    // }
    //
    // String introduction = null;
    //
    // if (Validator.isNotNull(script)) {
    //
    // try {
    // introduction = TemplateUtil.transform(contextObjects, script,
    // TemplateConstants.LANG_TYPE_FTL);
    // } catch (Exception e) {
    // throw new PortalException(e);
    // }
    // }
    //
    // StringBuilder sb = new StringBuilder();
    //
    // if (Validator.isNotNull(introduction)) {
    // sb.append("<div class=\"newsletter-introduction\">");
    // sb.append(introduction);
    // sb.append("</div>");
    // }
    //
    // if (article != null) {
    //
    // String languageId = (String) contextObjects.get("languageId");
    // if (Validator.isNull(languageId)) {
    // languageId = LanguageUtil.getLanguageId(Locale.US);
    // }
    //
    // JournalArticleDisplay articleDisplay = JournalArticleLocalServiceUtil
    // .getArticleDisplay(article, null, null, languageId, 1,
    // null, null);
    //
    // String content = articleDisplay.getContent();
    //
    // Company company = CompanyLocalServiceUtil.getCompany(article
    // .getCompanyId());
    // String virtualHostname = company.getVirtualHostname();
    //
    // Group group = GroupLocalServiceUtil.getGroup(article.getGroupId());
    // LayoutSet layoutSet = group.getPublicLayoutSet();
    //
    // if (layoutSet != null) {
    // if (Validator.isNotNull(layoutSet.getVirtualHostname())) {
    // virtualHostname = layoutSet.getVirtualHostname();
    // }
    // }
    //
    // // Prefix all local image sources with the group's virtualhost
    //
    // content = content.replaceAll("/documents", protocol
    // + virtualHostname + "/documents");
    //
    // sb.append("<div class=\"newsletter-content\">");
    // sb.append(content);
    // sb.append("</div>");
    //
    // }
    //
    // return sb.toString();
    //
    // }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        return mailingLocalService.search(userId, groupId, ownerUserId, keywords, start, end, sort);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String title, String description, int status,
            LinkedHashMap<String, Object> params, boolean andSearch, int start, int end, Sort sort)
            throws PortalException {

        return mailingLocalService.search(userId, groupId, ownerUserId, title, description, status, params,
                andSearch, start, end, sort);

    }

    // TODO
    // @Override
    // public long sendMailingsInBackground(long userId, String taskName, long
    // groupId, Map<String, String[]> parameterMap)
    // throws PortalException {
    //
    // // TODO: check permissions
    // return mailingLocalService.sendMailingsInBackground(userId, taskName,
    // groupId, parameterMap);
    // }

    @Override
    public Mailing updateMailing(long userId, long groupId, long mailingId, String title, String template,
            long newsletterId, String articleId, long articleGroupId, Date publishDate, Date sendDate, boolean sent,
            ServiceContext serviceContext) throws PortalException {

        MailingPermission.check(getPermissionChecker(), mailingId, ActionKeys.UPDATE);

        return mailingLocalService.updateMailing(userId, groupId, mailingId, title, template, newsletterId, articleId,
                articleGroupId, publishDate, sendDate, sent, serviceContext);

    }

    private static Log _log = LogFactoryUtil.getLog(MailingServiceImpl.class.getName());
}