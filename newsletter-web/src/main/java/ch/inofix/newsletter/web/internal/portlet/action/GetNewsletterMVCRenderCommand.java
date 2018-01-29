package ch.inofix.newsletter.web.internal.portlet.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ch.inofix.newsletter.exception.NoSuchNewsletterException;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2017-11-10 23:49
 * @modified 2017-11-10 23:49
 * @version 1.0.0
 *
 */
public abstract class GetNewsletterMVCRenderCommand implements MVCRenderCommand {

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        _log.info("render()");

        try {
            Newsletter newsletter = ActionUtil.getNewsletter(renderRequest);

            renderRequest.setAttribute(NewsletterWebKeys.NEWSLETTER, newsletter);
        } catch (Exception e) {
            if (e instanceof NoSuchNewsletterException || e instanceof PrincipalException) {

                SessionErrors.add(renderRequest, e.getClass());

                return "/error.jsp";

            } else {
                throw new PortletException(e);
            }
        }

        return getPath();
    }

    protected abstract String getPath();

    private static Log _log = LogFactoryUtil.getLog(GetNewsletterMVCRenderCommand.class.getName());

}
