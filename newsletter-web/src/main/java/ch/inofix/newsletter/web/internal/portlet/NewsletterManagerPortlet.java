package ch.inofix.newsletter.web.internal.portlet;

import java.io.IOException;
import java.util.Date;
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
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import aQute.bnd.annotation.metatype.Configurable;
import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.exception.NoSuchNewsletterException;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.MailingService;
import ch.inofix.newsletter.service.NewsletterService;
import ch.inofix.newsletter.service.SubscriberService;
import ch.inofix.newsletter.web.configuration.NewsletterManagerConfiguration;
import ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys;

/**
 * NewsletterManagerPortlet: MVC-Controller of the newsletter-manager.
 *
 * @author Christian Berndt
 * @created 2016-10-08 00:20
 * @modified 2017-10-14 13:58
 */
@Component(immediate = true, property = { 
		"com.liferay.portlet.css-class-wrapper=portlet-newsletter",
        "com.liferay.portlet.display-category=category.inofix", 
        "com.liferay.portlet.header-portlet-css=/css/main.css",
        "com.liferay.portlet.instanceable=false", 
        "javax.portlet.display-name=Newsletter",
        "javax.portlet.init-param.template-path=/", 
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + PortletKeys.NEWSLETTER_MANAGER, 
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" 
    }, 
	service = Portlet.class
)
public class NewsletterManagerPortlet extends MVCPortlet {

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        renderRequest.setAttribute(NewsletterManagerConfiguration.class.getName(), _newsletterManagerConfiguration);

        super.doView(renderRequest, renderResponse);
    }

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) {

        String className = ParamUtil.getString(actionRequest, "className");

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);


        try {
            
            if (cmd.equals(Constants.DELETE)) {
                
                if (Mailing.class.getName().equals(className)) {
                    
                    deleteMailings(actionRequest, actionResponse);
                    addSuccessMessage(actionRequest, actionResponse);
                    
                } else if (Newsletter.class.getName().equals(className)) {
                    
                    deleteNewsletters(actionRequest, actionResponse);
                    addSuccessMessage(actionRequest, actionResponse);
                    
                } else if (Subscriber.class.getName().equals(className)) {
                    
                    deleteSubscribers(actionRequest, actionResponse);                    
                    addSuccessMessage(actionRequest, actionResponse);
                    
                }

            } else if (cmd.equals(Constants.UPDATE)) {

                if (Mailing.class.getName().equals(className)) {
                    
                    updateMailing(actionRequest, actionResponse);
                    addSuccessMessage(actionRequest, actionResponse);
                    
                } else if (Newsletter.class.getName().equals(className)) {
                    
                    updateNewsletter(actionRequest, actionResponse);
                    addSuccessMessage(actionRequest, actionResponse);
                    
                } else if (Subscriber.class.getName().equals(className)) {
                    
                    updateSubscriber(actionRequest, actionResponse);
                    addSuccessMessage(actionRequest, actionResponse);
                    
                }
            }
        }

        catch (Exception e) {

            // TODO: report errors back to user
            _log.error(e);
        }

    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

		try {
			
			getMailing(renderRequest);
			getNewsletter(renderRequest);
			getSubscriber(renderRequest);
			
		} catch (Exception e) {
			if (e instanceof NoSuchResourceException || e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass());
			} else {
				throw new PortletException(e);
			}
		}

//        renderRequest.setAttribute(NewsletterManagerConfiguration.class.getName(), _newsletterManagerConfiguration);

        super.render(renderRequest, renderResponse);
    }

//    @Override
//    public void sendRedirect(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
//
//        // Disable the default sendRedirect-behaviour of LiferayPortlet in order
//        // to pass renderParameters via actionResponse's setRenderParameter()
//        // methods.
//    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        _newsletterManagerConfiguration = Configurable.createConfigurable(NewsletterManagerConfiguration.class,
                properties);
    }
    
    /**
    *
    * @param actionRequest
    * @param actionResponse
    * @throws Exception
    */
   protected void deleteMailings(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

       long mailingId = ParamUtil.getLong(actionRequest, "mailingId");

       long[] mailingIds = ParamUtil.getLongValues(actionRequest, "deleteMailingIds");

       if (mailingId > 0) {
           mailingIds = new long[] { mailingId };
       }

       for (long id : mailingIds) {
           _mailingService.deleteMailing(id);
       }

   }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.0
     * @throws Exception
     */
    protected void deleteNewsletters(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        long[] newsletterIds = ParamUtil.getLongValues(actionRequest, "deleteMailingIds");

        if (newsletterId > 0) {
            newsletterIds = new long[] { newsletterId };
        }

        for (long id : newsletterIds) {
            _newsletterService.deleteNewsletter(id);
        }

//        actionResponse.setRenderParameter("postDelete", "true");

    }
    
    /**
    *
    * @param actionRequest
    * @param actionResponse
    * @throws Exception
    */
   protected void deleteSubscribers(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

       long subscriberId = ParamUtil.getLong(actionRequest, "subscriberId");

       long[] subscriberIds = ParamUtil.getLongValues(actionRequest, "deleteSubscriberIds");

       if (subscriberId > 0) {
           subscriberIds = new long[] { subscriberId };
       }

       for (long id : subscriberIds) {
           _subscriberService.deleteSubscriber(id);
       }

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

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @param newsletter
     * @return
     * @throws Exception
     */
    protected String getEditMailingURL(ActionRequest actionRequest, ActionResponse actionResponse, Mailing mailing)
            throws Exception {

        return getEditURL(actionRequest, actionResponse, mailing.getMailingId(), "mailingId", "edit_mailing.jsp");
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

        return getEditURL(actionRequest, actionResponse, newsletter.getNewsletterId(), "newsletterId",
                "edit_newsletter.jsp");

    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @param newsletter
     * @return
     * @throws Exception
     */
    protected String getEditSubscriberURL(ActionRequest actionRequest, ActionResponse actionResponse,
            Subscriber subscriber) throws Exception {

        return getEditURL(actionRequest, actionResponse, subscriber.getNewsletterId(), "subscriberId",
                "edit_subscriber.jsp");

    }

    protected String getEditURL(ActionRequest actionRequest, ActionResponse actionResponse, long classPK,
            String classPKParam, String editJSP) throws Exception {

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        String editURL = getRedirect(actionRequest, actionResponse);

        if (Validator.isNull(editURL)) {
            editURL = PortalUtil.getLayoutFullURL(themeDisplay);
        }

        String namespace = actionResponse.getNamespace();
        String windowState = actionResponse.getWindowState().toString();

        editURL = HttpUtil.setParameter(editURL, "p_p_id", PortletKeys.NEWSLETTER_MANAGER);
        editURL = HttpUtil.setParameter(editURL, "p_p_state", windowState);
        editURL = HttpUtil.setParameter(editURL, namespace + "mvcPath", templatePath + editJSP);
        editURL = HttpUtil.setParameter(editURL, namespace + "redirect", getRedirect(actionRequest, actionResponse));
        editURL = HttpUtil.setParameter(editURL, namespace + "backURL", ParamUtil.getString(actionRequest, "backURL"));
        editURL = HttpUtil.setParameter(editURL, namespace + classPKParam, classPK);

        return editURL;
    }

    /**
     *
     * @param portletRequest
     * @throws Exception
     */
    protected void getMailing(PortletRequest portletRequest) throws Exception {

        long mailingId = ParamUtil.getLong(portletRequest, "mailingId");
        boolean postDelete = ParamUtil.getBoolean(portletRequest, "postDelete");

        if (mailingId <= 0 || postDelete) {
            return;
        }

        Mailing mailing = _mailingService.getMailing(mailingId);

        portletRequest.setAttribute(NewsletterWebKeys.MAILING, mailing);
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

    /**
     *
     * @param portletRequest
     * @throws Exception
     */
    protected void getSubscriber(PortletRequest portletRequest) throws Exception {

        _log.info("getSubscriber()");
        
        long subscriberId = ParamUtil.getLong(portletRequest, "subscriberId");
        boolean postDelete = ParamUtil.getBoolean(portletRequest, "postDelete");

        if (subscriberId <= 0 || postDelete) {
            return;
        }

        Subscriber subscriber = _subscriberService.getSubscriber(subscriberId);

        portletRequest.setAttribute(NewsletterWebKeys.SUBSCRIBER, subscriber);
    }

    @Reference
    protected void setMailingService(MailingService mailingService) {
        this._mailingService = mailingService;
    }

    @Reference
    protected void setNewsletterService(NewsletterService newsletterService) {
        this._newsletterService = newsletterService;
    }

    @Reference
    protected void setSubscriberService(SubscriberService subscriberService) {
        this._subscriberService = subscriberService;
    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws Exception
     */
    protected void updateMailing(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long mailingId = ParamUtil.getLong(actionRequest, "mailingId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Mailing.class.getName(), actionRequest);

        String articleId = ParamUtil.getString(actionRequest, "articleId");
        long articleGroupId = ParamUtil.getLong(actionRequest, "articleGroupId");
        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");
        // TODO:
        Date publishDate = null;
        // TODO:
        Date sendDate = null;
        boolean sent = ParamUtil.getBoolean(actionRequest, "sent");
        int status = ParamUtil.getInteger(actionRequest, "status");

        String title = ParamUtil.getString(actionRequest, "title");
        String template = ParamUtil.getString(actionRequest, "template");

        Mailing mailing = null;

        if (mailingId <= 0) {

            // Add mailing

            mailing = _mailingService.addMailing(title, template, newsletterId, articleId, articleGroupId, publishDate,
                    sendDate, status, serviceContext);
        } else {

            // Update mailing

            mailing = _mailingService.updateMailing(mailingId, title, template, newsletterId, articleId, articleGroupId,
                    publishDate, sendDate, sent, status, serviceContext);

        }

        String redirect = getEditMailingURL(actionRequest, actionResponse, mailing);
        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

        actionRequest.setAttribute(NewsletterWebKeys.MAILING, mailing);
    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @since 1.0.0
     * @throws Exception
     */
    protected void updateNewsletter(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");

        String fromAddress = ParamUtil.getString(actionRequest, "fromAddress");
        String fromName = ParamUtil.getString(actionRequest, "fromName");
        String template = ParamUtil.getString(actionRequest, "template");
        String title = ParamUtil.getString(actionRequest, "title");
        boolean useHttps = ParamUtil.getBoolean(actionRequest, "useHttps");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Newsletter.class.getName(), actionRequest);

        Newsletter newsletter = null;

        if (newsletterId <= 0) {
            newsletter = _newsletterService.addNewsletter(title, template, fromAddress, fromName, useHttps,
                    serviceContext);
        } else {
            newsletter = _newsletterService.updateNewsletter(newsletterId, title, template, fromAddress, fromName,
                    useHttps, serviceContext);
        }

        String redirect = getEditNewsletterURL(actionRequest, actionResponse, newsletter);
        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

        actionRequest.setAttribute(NewsletterWebKeys.NEWSLETTER, newsletter);

    }

    protected void updateSubscriber(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        long subscriberId = ParamUtil.getLong(actionRequest, "subscriberId");

        String email = ParamUtil.getString(actionRequest, "email");
        String firstName = ParamUtil.getString(actionRequest, "firstName");
        String gender = ParamUtil.getString(actionRequest, "gender");
        String lastName = ParamUtil.getString(actionRequest, "lastName");
        String middleName = ParamUtil.getString(actionRequest, "middleName");
        long newsletterId = ParamUtil.getLong(actionRequest, "newsletterId");
        String salutation = ParamUtil.getString(actionRequest, "salutation");
        String title = ParamUtil.getString(actionRequest, "title");
        int status = ParamUtil.getInteger(actionRequest, "status");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Subscriber.class.getName(), actionRequest);

        Subscriber subscriber = null;

        if (subscriberId <= 0) {
            subscriber = _subscriberService.addSubscriber(email, firstName, gender, lastName, middleName, newsletterId,
                    salutation, title, status, serviceContext);
        } else {
            subscriber = _subscriberService.updateSubscriber(subscriberId, email, firstName, gender, lastName,
                    middleName, newsletterId, salutation, title, status, serviceContext);
        }

        String redirect = getEditSubscriberURL(actionRequest, actionResponse, subscriber);
        actionRequest.setAttribute(WebKeys.REDIRECT, redirect);

        actionRequest.setAttribute(NewsletterWebKeys.SUBSCRIBER, subscriber);

    }

    private MailingService _mailingService;
    private NewsletterService _newsletterService;
    private SubscriberService _subscriberService;

    private volatile NewsletterManagerConfiguration _newsletterManagerConfiguration;

    private static Log _log = LogFactoryUtil.getLog(NewsletterManagerPortlet.class.getName());

}