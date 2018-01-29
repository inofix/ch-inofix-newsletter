package ch.inofix.newsletter.web.internal.portlet.action;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterServiceUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-11 00:11
 * @modified 2017-11-13 22:26
 * @version 1.0.1
 *
 */
public class ActionUtil {

    public static Newsletter getNewsletter(HttpServletRequest request)
            throws Exception {

        long newsletterId = ParamUtil.getLong(request, "newsletterId");

        Newsletter newsletter = null;

        if (newsletterId > 0) {
            newsletter = NewsletterServiceUtil.getNewsletter(newsletterId);

            // TODO: Add TrashBin support
            // if (newsletter.isInTrash()) {
            // throw new NoSuchNewsletterException("{newsletterId=" +
            // newsletterId + "}");
            // }
        }

        return newsletter;
    }

    public static Newsletter getNewsletter(PortletRequest portletRequest)
            throws Exception {

        HttpServletRequest request = PortalUtil
                .getHttpServletRequest(portletRequest);

        return getNewsletter(request);
    }

}
