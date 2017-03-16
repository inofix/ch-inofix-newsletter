package ch.inofix.newsletter.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-03-16 16:26
 * @modified 2017-03-16 16:26
 * @version 1.0.0
 *
 */
public class SubscriberSearchTerms extends SubscriberDisplayTerms {

	public SubscriberSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		createDate = DAOParamUtil.getString(portletRequest, CREATE_DATE);
		modifiedDate = DAOParamUtil.getString(portletRequest, MODIFIED_DATE);
		sendDate = DAOParamUtil.getString(portletRequest, SEND_DATE);
		title = DAOParamUtil.getString(portletRequest, TITLE);
		userName = DAOParamUtil.getString(portletRequest, USER_NAME);
	}

}
