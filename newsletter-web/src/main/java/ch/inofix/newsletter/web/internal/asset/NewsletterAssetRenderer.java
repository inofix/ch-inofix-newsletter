package ch.inofix.newsletter.web.internal.asset;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;

import ch.inofix.newsletter.constants.PortletKeys;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.permission.NewsletterPermission;
import ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys;;

/**
 *
 * @author Christian Berndt
 * @created 2017-03-21 13:51
 * @modified 2017-07-09 17:39
 * @version 1.0.1
 *
 */
public class NewsletterAssetRenderer extends BaseJSPAssetRenderer<Newsletter> implements TrashRenderer {

    public NewsletterAssetRenderer(Newsletter newsletter) {
        _newsletter = newsletter;
    }

    @Override
    public Newsletter getAssetObject() {
        return _newsletter;
    }

    @Override
    public String getClassName() {
        return Newsletter.class.getName();
    }

    @Override
    public long getClassPK() {
        return _newsletter.getNewsletterId();
    }

    @Override
    public long getGroupId() {
        return _newsletter.getGroupId();
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT)) {
            return "/asset/" + template + ".jsp";
        } else {
            return null;
        }
    }

    @Override
    public String getPortletId() {
        AssetRendererFactory<Newsletter> assetRendererFactory = getAssetRendererFactory();

        return assetRendererFactory.getPortletId();
    }

    @Override
    public int getStatus() {
        return _newsletter.getStatus();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return _newsletter.getTitle();
    }

    @Override
    public String getTitle(Locale locale) {
        return _newsletter.getTitle();
    }

    @Override
    public String getType() {
        return NewsletterAssetRendererFactory.TYPE;
    }

    @Override
    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse) throws Exception {

        PortletURL portletURL = locateNewsletterManager(liferayPortletRequest);

        return portletURL;

    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) {

        try {

            long portletPlid = PortalUtil.getPlidFromPortletId(_newsletter.getGroupId(), false,
                    PortletKeys.NEWSLETTER_MANAGER);

            PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(portletPlid, PortletKeys.NEWSLETTER_MANAGER,
                    PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_newsletter.jsp");

            portletURL.setParameter("newsletterId", String.valueOf(_newsletter.getNewsletterId()));

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) {

        try {

            PortletURL portletURL = locateNewsletterManager(liferayPortletRequest);

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public long getUserId() {
        return _newsletter.getUserId();
    }

    @Override
    public String getUserName() {
        return _newsletter.getUserName();
    }

    @Override
    public String getUuid() {
        return _newsletter.getUuid();
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) {

        return NewsletterPermission.contains(permissionChecker, _newsletter, ActionKeys.VIEW);
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {

        request.setAttribute(NewsletterWebKeys.NEWSLETTER, _newsletter);

        return super.include(request, response, template);
    }

    private PortletURL locateNewsletterManager(LiferayPortletRequest liferayPortletRequest) throws PortalException {

        long portletPlid = PortalUtil.getPlidFromPortletId(_newsletter.getGroupId(), false, PortletKeys.NEWSLETTER_MANAGER);

        PortletURL portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, PortletKeys.NEWSLETTER_MANAGER,
                portletPlid, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcPath", "/edit_newsletter.jsp");

        portletURL.setParameter("newsletterId", String.valueOf(_newsletter.getNewsletterId()));

        return portletURL;
    }

    private static final Log _log = LogFactoryUtil.getLog(NewsletterAssetRenderer.class);

    private final Newsletter _newsletter;

}
