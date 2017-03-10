package ch.inofix.newsletter.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 *
 * @author Christian Berndt
 * @created 2016-10-16 23:27
 * @modified 2017-03-10 17:40
 * @version 1.0.2
 *
 */
public class MailingSearchTerms extends MailingDisplayTerms {

    public MailingSearchTerms(PortletRequest portletRequest) {
        super(portletRequest);

        createDate = DAOParamUtil.getString(portletRequest, CREATE_DATE);
        modifiedDate = DAOParamUtil.getString(portletRequest, MODIFIED_DATE);
        sendDate = DAOParamUtil.getString(portletRequest, SEND_DATE);
        title = DAOParamUtil.getString(portletRequest, TITLE);
        userName = DAOParamUtil.getString(portletRequest, USER_NAME);
    }

}
