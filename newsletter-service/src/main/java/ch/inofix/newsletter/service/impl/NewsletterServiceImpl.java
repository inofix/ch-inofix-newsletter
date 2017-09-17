/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.security.ActionKeys;
import ch.inofix.newsletter.service.base.NewsletterServiceBaseImpl;
import ch.inofix.newsletter.service.permission.NewsletterPermission;
import ch.inofix.newsletter.service.permission.NewsletterPortletPermission;

/**
 * The implementation of the newsletter remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.NewsletterService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-08 01:25
 * @modified 2017-09-17 13:12
 * @version 1.1.0
 * @see ch.inofix.portlet.newsletter.service.base.NewsletterServiceBaseImpl
 * @see ch.inofix.portlet.newsletter.service.NewsletterServiceUtil
 */
@ProviderType
public class NewsletterServiceImpl extends NewsletterServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     * 
     * Never reference this interface directly. Always use {@link
     * ch.inofix.portlet.newsletter.service.NewsletterServiceUtil} to access the
     * newsletter remote service.
     */

    /**
     *
     * @param userId
     * @param title
     * @param template
     * @param serviceContext
     * @return
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    @Override
    public Newsletter addNewsletter(long userId, String title, String template, String fromAddress, String fromName,
            boolean useHttps, ServiceContext serviceContext) throws PortalException {

        NewsletterPortletPermission.check(getPermissionChecker(), serviceContext.getScopeGroupId(),
                ActionKeys.ADD_NEWSLETTER);

        return newsletterLocalService.addNewsletter(userId, title, template, fromAddress, fromName, useHttps,
                serviceContext);

    }

    /**
     *
     * @return
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    @Override
    public Newsletter createNewsletter() throws PortalException {

        // Create an empty newsletter - no permission check required
        return newsletterLocalService.createNewsletter(0);

    }

    /**
     * Delete a specific newsletter and return the deleted newsletter.
     *
     * @param newsletterId
     * @return the deleted newsletter
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    @Override
    public Newsletter deleteNewsletter(long newsletterId) throws PortalException {

        NewsletterPermission.check(getPermissionChecker(), newsletterId, ActionKeys.DELETE);

        Newsletter newsletter = newsletterLocalService.deleteNewsletter(newsletterId);

        return newsletter;

    }

    /**
     * Return the newsletter.
     *
     * @param newsletterId
     * @return the latest version of a newsletter.
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    @Override
    public Newsletter getNewsletter(long newsletterId) throws PortalException {

        NewsletterPermission.check(getPermissionChecker(), newsletterId, ActionKeys.VIEW);

        return newsletterLocalService.getNewsletter(newsletterId);

    }
    
    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        return newsletterLocalService.search(userId, groupId, ownerUserId, keywords, start, end, sort);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String title, String fromAddress, String fromName, int status,
            LinkedHashMap<String, Object> params, boolean andSearch, int start, int end, Sort sort)
            throws PortalException {

        return newsletterLocalService.search(userId, groupId, ownerUserId, title, fromAddress, fromName, status, params,
                andSearch, start, end, sort);

    }

    @Override
    public Newsletter updateNewsletter(long userId, long newsletterId, String title, String template,
            String fromAddress, String fromName, boolean useHttps, ServiceContext serviceContext)
            throws PortalException {

        NewsletterPermission.check(getPermissionChecker(), newsletterId, ActionKeys.UPDATE);

        return newsletterLocalService.updateNewsletter(userId, serviceContext.getScopeGroupId(), title, template,
                fromAddress, fromName, useHttps, serviceContext);

    }

    private static Log _log = LogFactoryUtil.getLog(NewsletterServiceImpl.class.getName());
}