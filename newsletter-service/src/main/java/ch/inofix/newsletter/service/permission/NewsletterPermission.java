package ch.inofix.newsletter.service.permission;

import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterLocalServiceUtil;
import ch.inofix.newsletter.service.permission.NewsletterPermission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 *
 * @author Christian Berndt
 * @created 2016-10-08 00:32
 * @modified 2018-01-29 14:21
 * @version 1.0.3
 *
 */
public class NewsletterPermission {

    public static void check(PermissionChecker permissionChecker, Newsletter newsletter, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, newsletter, actionId)) {
            throw new PrincipalException();
        }
    }

    public static void check(PermissionChecker permissionChecker, long newsletterId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, newsletterId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, Newsletter newsletter, String actionId) {

        if (permissionChecker.hasOwnerPermission(newsletter.getCompanyId(), Newsletter.class.getName(),
                newsletter.getNewsletterId(), newsletter.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(newsletter.getGroupId(), Newsletter.class.getName(),
                String.valueOf(newsletter.getNewsletterId()), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long newsletterId, String actionId){

        Newsletter newsletter;
        try {
            newsletter = NewsletterLocalServiceUtil.getNewsletter(newsletterId);
            return contains(permissionChecker, newsletter, actionId);
        } catch (PortalException e) {
            _log.error(e);
        }

        return false;
    }

    private static final Log _log = LogFactoryUtil.getLog(NewsletterPermission.class.getName());

}
