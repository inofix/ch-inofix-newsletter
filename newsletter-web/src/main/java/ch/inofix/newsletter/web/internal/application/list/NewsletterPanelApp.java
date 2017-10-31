package ch.inofix.newsletter.web.internal.application.list;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;

import ch.inofix.newsletter.constants.PortletKeys;

/**
 *
 * @author Christian Berndt
 * @created 2017-11-01 00:15
 * @modified 2017-11-01 00:15
 * @version 1.0.0
 *
 */
@Component(
    immediate = true, 
    property = { 
        "panel.app.order:Integer=800",
        "panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT 
    }, 
    service = PanelApp.class
)
public class NewsletterPanelApp extends BasePanelApp {

    @Override
    public String getPortletId() {
        return PortletKeys.NEWSLETTER_MANAGER;
    }

    @Override
    @Reference(target = "(javax.portlet.name=" + PortletKeys.NEWSLETTER_MANAGER + ")", unbind = "-")
    public void setPortlet(Portlet portlet) {
        super.setPortlet(portlet);
    }

}
