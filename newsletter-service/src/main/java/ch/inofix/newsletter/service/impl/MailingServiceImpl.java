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

import com.liferay.portal.kernel.exception.PortalException;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.security.ActionKeys;
import ch.inofix.newsletter.service.base.MailingServiceBaseImpl;
import ch.inofix.newsletter.service.permission.MailingPermission;

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
 * @modified 2017-03-11 00:16
 * @version 1.1.4
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
    public Mailing getMailing(long mailingId) throws PortalException {

        MailingPermission.check(getPermissionChecker(), mailingId, ActionKeys.VIEW);

        return mailingLocalService.getMailing(mailingId);

    }
}