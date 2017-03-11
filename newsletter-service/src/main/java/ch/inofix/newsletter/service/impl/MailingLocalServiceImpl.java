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

import aQute.bnd.annotation.ProviderType;

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
    
    
}