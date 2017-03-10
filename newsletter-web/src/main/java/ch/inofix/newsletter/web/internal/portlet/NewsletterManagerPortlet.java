package ch.inofix.newsletter.web.internal.portlet;

import javax.portlet.Portlet;
import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.inofix",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Newsletter",
		"javax.portlet.init-param.template-path=/", 
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class NewsletterManagerPortlet extends MVCPortlet {

}