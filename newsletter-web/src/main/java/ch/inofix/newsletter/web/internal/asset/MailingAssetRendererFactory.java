package ch.inofix.newsletter.web.internal.asset;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.constants.NewsletterActionKeys;
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.service.MailingLocalService;
import ch.inofix.newsletter.service.permission.MailingPermission;
import ch.inofix.newsletter.service.permission.NewsletterPortletPermission;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-26 23:11
 * @modified 2018-01-29 15:08
 * @version 1.0.1
 *
 */
@Component(
    immediate = true, 
    property = {"javax.portlet.name=" + PortletKeys.NEWSLETTER_MANAGER }, 
    service = AssetRendererFactory.class
)
public class MailingAssetRendererFactory
        extends BaseAssetRendererFactory<Mailing> {

    public static final String TYPE = "mailing";

    public MailingAssetRendererFactory() {

        setCategorizable(true);
        setClassName(Mailing.class.getName());
        setLinkable(true);
        setPortletId(PortletKeys.NEWSLETTER_MANAGER);
        setSearchable(true);
        setSelectable(true);

    }

    @Override
    public AssetRenderer<Mailing> getAssetRenderer(long classPK, int type)
            throws PortalException {

        Mailing mailing = _mailingLocalService.getMailing(classPK);

        MailingAssetRenderer mailingAssetRenderer = new MailingAssetRenderer(
                mailing);

        mailingAssetRenderer.setAssetRendererType(type);
        mailingAssetRenderer.setServletContext(_servletContext);

        return mailingAssetRenderer;

    }

    @Override
    public String getClassName() {
        return Mailing.class.getName();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse)
            throws PortalException {

        ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest
                .getAttribute(WebKeys.THEME_DISPLAY);

        User user = themeDisplay.getUser();

        Group group = user.getGroup();

        if (group != null) {

            long portletPlid = PortalUtil.getPlidFromPortletId(
                    group.getGroupId(), false, PortletKeys.NEWSLETTER_MANAGER);

            PortletURL portletURL = PortletURLFactoryUtil.create(
                    liferayPortletRequest, PortletKeys.NEWSLETTER_MANAGER,
                    portletPlid, PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_task_record.jsp");

            String redirect = (String) liferayPortletRequest
                    .getAttribute("redirect");

            if (Validator.isNotNull(redirect)) {
                portletURL.setParameter("redirect", redirect);
            }

            return portletURL;

        } else {

            return null;

        }
    }

    @Override
    public boolean hasAddPermission(PermissionChecker permissionChecker,
            long groupId, long classTypeId) throws Exception {

        return NewsletterPortletPermission.contains(permissionChecker, groupId,
                NewsletterActionKeys.ADD_MAILING);
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker,
            long classPK, String actionId) throws Exception {

        Mailing mailing = _mailingLocalService.getMailing(classPK);

        return MailingPermission.contains(permissionChecker,
                mailing.getMailingId(), actionId);
    }

    @Reference(target = "(osgi.web.symbolicname=ch.inofix.newsletter.web)", unbind = "-")
    public void setServletContext(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    @Reference(unbind = "-")
    protected void setMailingLocalService(
            MailingLocalService mailingLocalService) {
        _mailingLocalService = mailingLocalService;
    }

    private static final Log _log = LogFactoryUtil
            .getLog(MailingAssetRendererFactory.class);

    private MailingLocalService _mailingLocalService;
    private ServletContext _servletContext;

}
