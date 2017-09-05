package ch.inofix.newsletter.web.internal.portlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

/**
 * NewsletterSubscriptionPortlet: MVC-Controller of the newsletter-subscription.
 *
 * @author Christian Berndt
 * @created 2017-09-05 22:59
 * @modified 2017-09-05 22:59
 * @version 1.0.0
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.inofix",
        "com.liferay.portlet.instanceable=false", "javax.portlet.display-name=Newsletter Subscription",
        "javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/subscription/view.jsp",
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class NewsletterSubscriptionPortlet extends MVCPortlet {

}
