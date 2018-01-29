package ch.inofix.newsletter.web.internal.portlet.action;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import ch.inofix.newsletter.constants.PortletKeys;

/**
 * 
 * @author Christian Berndt
 * @created 2018-01-29 13:13
 * @modified 2018-01-29 13:13
 * @version 1.0.0
 *
 */
@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + PortletKeys.NEWSLETTER_MANAGER,
        "mvc.command.name=editNewsletter"
    },
    service = MVCRenderCommand.class
)
public class EditNewsletterMVCRenderCommand
        extends GetNewsletterMVCRenderCommand {

    @Override
    protected String getPath() {

        return "/edit_newsletter.jsp";
    }
}
