package ch.inofix.newsletter.web.internal.portlet.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.exception.NoSuchNewsletterException;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterService;

/**
 * 
 * @author Christian Berndt
 * @created 2018-01-29 13:26
 * @modified 2018-01-29 13:26
 * @version 1.0.0
 *
 */
@Component(
    property = {
        "javax.portlet.name=" + PortletKeys.NEWSLETTER_MANAGER,
        "mvc.command.name=editNewsletter"
    },
    service = MVCActionCommand.class
)
public class EditNewsletterMVCActionCommand extends BaseMVCActionCommand {

    protected void deleteNewsletters(ActionRequest actionRequest)
            throws Exception {

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        long[] newsletterIds = ParamUtil.getLongValues(actionRequest,
                "deleteNewsletterIds");

        if (newsletterId > 0) {
            newsletterIds = new long[] { newsletterId };
        }

        for (long id : newsletterIds) {
            _newsletterService.deleteNewsletter(id);
        }

    }

    @Override
    protected void doProcessAction(ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

        _log.info("doProcessAction");
        _log.info("cmd = " + cmd);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
                .getAttribute(WebKeys.THEME_DISPLAY);

        Newsletter newsletter = null;
        try {

            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                newsletter = updateNewsletter(actionRequest);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteNewsletters(actionRequest);
            }

            if (Validator.isNotNull(cmd)) {
                String redirect = ParamUtil.getString(actionRequest,
                        "redirect");
                if (newsletter != null) {

                    redirect = getSaveAndContinueRedirect(actionRequest,
                            newsletter, themeDisplay.getLayout(), redirect);

                    sendRedirect(actionRequest, actionResponse, redirect);
                }
            }

        } catch (NoSuchNewsletterException | PrincipalException e) {

            SessionErrors.add(actionRequest, e.getClass());

            actionResponse.setRenderParameter("mvcPath", "/error.jsp");

            // TODO: Define set of exceptions reported back to user. For an
            // example, see EditCategoryMVCActionCommand.java.

        } catch (Exception e) {

            SessionErrors.add(actionRequest, e.getClass());
        }
    }

    protected String getSaveAndContinueRedirect(ActionRequest actionRequest,
            Newsletter newsletter, Layout layout, String redirect)
            throws Exception {

        PortletConfig portletConfig = (PortletConfig) actionRequest
                .getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
                actionRequest, portletConfig.getPortletName(), layout,
                PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcRenderCommandName", "editNewsletter");

        portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
        portletURL.setParameter("redirect", redirect, false);
        portletURL.setParameter("groupId",
                String.valueOf(newsletter.getGroupId()), false);
        portletURL.setParameter("newsletterId",
                String.valueOf(newsletter.getNewsletterId()), false);
        portletURL.setWindowState(actionRequest.getWindowState());

        return portletURL.toString();
    }

    @Reference(unbind = "-")
    protected void setNewsletterService(NewsletterService newsletterService) {
        this._newsletterService = newsletterService;
    }

    protected Newsletter updateNewsletter(ActionRequest actionRequest)
            throws Exception {

        _log.info("updateNewsletter");

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        String fromAddress = ParamUtil.getString(actionRequest, "fromAddress");
        String fromName = ParamUtil.getString(actionRequest, "fromName");
        String template = ParamUtil.getString(actionRequest, "template");
        String title = ParamUtil.getString(actionRequest, "title");
        boolean useHttps = ParamUtil.getBoolean(actionRequest, "useHttps");

        ServiceContext serviceContext = ServiceContextFactory
                .getInstance(Newsletter.class.getName(), actionRequest);

        Newsletter newsletter = null;

        if (newsletterId <= 0) {

            // Add newsletter

            newsletter = _newsletterService.addNewsletter(title, template,
                    fromAddress, fromName, useHttps, serviceContext);

        } else {

            // Update newsletter

            newsletter = _newsletterService.updateNewsletter(newsletterId,
                    title, template, fromAddress, fromName, useHttps,
                    serviceContext);
        }

        return newsletter;
    }

    private NewsletterService _newsletterService;

    private static Log _log = LogFactoryUtil
            .getLog(EditNewsletterMVCActionCommand.class.getName());

}
