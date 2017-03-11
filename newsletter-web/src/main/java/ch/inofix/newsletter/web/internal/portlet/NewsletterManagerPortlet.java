package ch.inofix.newsletter.web.internal.portlet;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import aQute.bnd.annotation.metatype.Configurable;
import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.exception.NoSuchNewsletterException;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterService;
import ch.inofix.newsletter.web.configuration.NewsletterManagerConfiguration;
import ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys;

/**
 * NewsletterManagerPortlet: MVC-Controller of the newsletter-manager.
 *
 * @author Christian Berndt
 * @created 2016-10-08 00:20
 * @modified 2017-03-10 23:44
 * @version 1.2.0
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.instanceable=false", "javax.portlet.display-name=Newsletter",
        "javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class NewsletterManagerPortlet extends MVCPortlet {

    /**
     * 
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.0
     * @throws Exception
     */
    public void deleteNewsletter(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        _newsletterService.deleteNewsletter(newsletterId);

        actionResponse.setRenderParameter("postDelete", "true");

    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

//        try {
//            getNewsletter(renderRequest);
//        } catch (Exception e) {
//            if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
//                SessionErrors.add(renderRequest, e.getClass());
//            } else {
//                throw new PortletException(e);
//            }
//        }

        renderRequest.setAttribute(NewsletterManagerConfiguration.class.getName(), _newsletterManagerConfiguration);

        super.render(renderRequest, renderResponse);
    }

    @Override
    public void sendRedirect(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {

        // Disable the default sendRedirect-behaviour of LiferayPortlet in order
        // to pass renderParameters via actionResponse's setRenderParameter()
        // methods.
    }

    /**
     * 
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.0
     * @throws Exception
     */
    public void updateNewsletter(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        String fromAddress = ParamUtil.getString(actionRequest, "fromAddress");
        String fromName = ParamUtil.getString(actionRequest, "fromName");
        String template = ParamUtil.getString(actionRequest, "template");
        String title = ParamUtil.getString(actionRequest, "title");
        boolean useHttps = ParamUtil.getBoolean(actionRequest, "useHttps");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Newsletter.class.getName(), actionRequest);

        long userId = serviceContext.getUserId();

        Newsletter newsletter = null;

        if (newsletterId <= 0) {
            newsletter = _newsletterService.addNewsletter(userId, title, template, fromAddress, fromName, useHttps,
                    serviceContext);
        } else {
            newsletter = _newsletterService.updateNewsletter(userId, newsletterId, title, template, fromAddress,
                    fromName, useHttps, serviceContext);
        }

        String redirect = getEditNewsletterURL(actionRequest, actionResponse, newsletter);
        String tabs1 = ParamUtil.get(actionRequest, "tabs1", "settings");

        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
        actionRequest.setAttribute(NewsletterWebKeys.NEWSLETTER, newsletter);
        actionResponse.setRenderParameter("tabs1", tabs1);

    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        _newsletterManagerConfiguration = Configurable.createConfigurable(NewsletterManagerConfiguration.class,
                properties);
    }

    /**
     * 
     */
    @Override
    protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        if (SessionErrors.contains(renderRequest, PrincipalException.getNestedClasses())
                || SessionErrors.contains(renderRequest, NoSuchNewsletterException.class)) {
            include("/error.jsp", renderRequest, renderResponse);
        } else {
            super.doDispatch(renderRequest, renderResponse);
        }
    }

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        renderRequest.setAttribute(NewsletterManagerConfiguration.class.getName(), _newsletterManagerConfiguration);

        super.doView(renderRequest, renderResponse);
    }

    /**
     * 
     * @param actionRequest
     * @param actionResponse
     * @param newsletter
     * @return
     * @throws Exception
     */
    protected String getEditNewsletterURL(ActionRequest actionRequest, ActionResponse actionResponse,
            Newsletter newsletter) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String editNewsletterURL = getRedirect(actionRequest, actionResponse);

        if (Validator.isNull(editNewsletterURL)) {
            editNewsletterURL = PortalUtil.getLayoutFullURL(themeDisplay);
        }

        String namespace = actionResponse.getNamespace();
        String windowState = actionResponse.getWindowState().toString();

        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, "p_p_id", PortletKeys.NEWSLETTER_MANAGER);
        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, "p_p_state", windowState);
        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, namespace + "mvcPath",
                templatePath + "edit_newsletter.jsp");
        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, namespace + "redirect",
                getRedirect(actionRequest, actionResponse));
        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, namespace + "backURL",
                ParamUtil.getString(actionRequest, "backURL"));
        editNewsletterURL = HttpUtil.setParameter(editNewsletterURL, namespace + "newsletterId",
                newsletter.getNewsletterId());

        return editNewsletterURL;
    }

    /**
     * 
     * @param portletRequest
     * @throws Exception
     */
    protected void getNewsletter(PortletRequest portletRequest) throws Exception {

        long newsletterId = ParamUtil.getLong(portletRequest, "newsletterId");
        boolean postDelete = ParamUtil.getBoolean(portletRequest, "postDelete");

        if (newsletterId <= 0 || postDelete) {
            return;
        }

        Newsletter newsletter = _newsletterService.getNewsletter(newsletterId);

        portletRequest.setAttribute(NewsletterWebKeys.NEWSLETTER, newsletter);
    }

    @org.osgi.service.component.annotations.Reference
    protected void setNewsletterService(NewsletterService newsletterService) {
        this._newsletterService = newsletterService;
    }

    private NewsletterService _newsletterService;

    private volatile NewsletterManagerConfiguration _newsletterManagerConfiguration;

    private static Log _log = LogFactoryUtil.getLog(NewsletterManagerPortlet.class.getName());

}