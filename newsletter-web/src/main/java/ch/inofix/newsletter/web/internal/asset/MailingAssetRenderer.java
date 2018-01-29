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
import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.service.permission.MailingPermission;
import ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys;;

/**
 *
 * @author Christian Berndt
 * @created 2018-01-29 14:39
 * @modified 2018-01-29 14:39
 * @version 1.0.0
 *
 */
public class MailingAssetRenderer extends BaseJSPAssetRenderer<Mailing>
        implements TrashRenderer {

    public MailingAssetRenderer(Mailing mailing) {
        _mailing = mailing;
    }

    @Override
    public Mailing getAssetObject() {
        return _mailing;
    }

    @Override
    public String getClassName() {
        return Mailing.class.getName();
    }

    @Override
    public long getClassPK() {
        return _mailing.getMailingId();
    }

    @Override
    public long getGroupId() {
        return _mailing.getGroupId();
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT)
                || template.equals(TEMPLATE_FULL_CONTENT)) {
            return "/asset/" + template + ".jsp";
        } else {
            return null;
        }
    }

    @Override
    public String getPortletId() {
        AssetRendererFactory<Mailing> assetRendererFactory = getAssetRendererFactory();

        return assetRendererFactory.getPortletId();
    }

    @Override
    public int getStatus() {
        return _mailing.getStatus();
    }

    @Override
    public String getSummary(PortletRequest portletRequest,
            PortletResponse portletResponse) {
        return _mailing.getTitle();
    }

    @Override
    public String getTitle(Locale locale) {
        return _mailing.getTitle();
    }

    @Override
    public String getType() {
        return MailingAssetRendererFactory.TYPE;
    }

    @Override
    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse) throws Exception {

        PortletURL portletURL = locateMailingManager(liferayPortletRequest);

        return portletURL;

    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse,
            WindowState windowState) {

        try {

            long portletPlid = PortalUtil.getPlidFromPortletId(
                    _mailing.getGroupId(), false,
                    PortletKeys.NEWSLETTER_MANAGER);

            PortletURL portletURL = liferayPortletResponse
                    .createLiferayPortletURL(portletPlid,
                            PortletKeys.NEWSLETTER_MANAGER,
                            PortletRequest.RENDER_PHASE);

            portletURL.setParameter("mvcPath", "/edit_mailing.jsp");

            portletURL.setParameter("mailingId",
                    String.valueOf(_mailing.getMailingId()));

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getURLViewInContext(
            LiferayPortletRequest liferayPortletRequest,
            LiferayPortletResponse liferayPortletResponse,
            String noSuchEntryRedirect) {

        try {

            PortletURL portletURL = locateMailingManager(liferayPortletRequest);

            return portletURL.toString();

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public long getUserId() {
        return _mailing.getUserId();
    }

    @Override
    public String getUserName() {
        return _mailing.getUserName();
    }

    @Override
    public String getUuid() {
        return _mailing.getUuid();
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) {

        return MailingPermission.contains(permissionChecker, _mailing,
                ActionKeys.VIEW);
    }

    @Override
    public boolean include(HttpServletRequest request,
            HttpServletResponse response, String template) throws Exception {

        request.setAttribute(NewsletterWebKeys.MAILING, _mailing);

        return super.include(request, response, template);
    }

    private PortletURL locateMailingManager(
            LiferayPortletRequest liferayPortletRequest)
            throws PortalException {

        long portletPlid = PortalUtil.getPlidFromPortletId(
                _mailing.getGroupId(), false, PortletKeys.NEWSLETTER_MANAGER);

        PortletURL portletURL = PortletURLFactoryUtil.create(
                liferayPortletRequest, PortletKeys.NEWSLETTER_MANAGER,
                portletPlid, PortletRequest.RENDER_PHASE);

        portletURL.setParameter("mvcPath", "/edit_mailing.jsp");

        portletURL.setParameter("mailingId",
                String.valueOf(_mailing.getMailingId()));

        return portletURL;
    }

    private static final Log _log = LogFactoryUtil
            .getLog(MailingAssetRenderer.class);

    private final Mailing _mailing;

}
