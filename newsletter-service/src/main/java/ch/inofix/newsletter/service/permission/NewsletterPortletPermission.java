package ch.inofix.newsletter.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import ch.inofix.newsletter.constants.PortletKeys;


/**
 *
 * @author Christian Berndt
 * @created 2016-10-08 00:34
 * @modified 2018-01-29 11:29
 * @version 1.0.2
 *
 */
public class NewsletterPortletPermission extends BaseResourcePermissionChecker {

    public static final String RESOURCE_NAME = "ch.inofix.newsletter";

    public static void check(PermissionChecker permissionChecker, long groupId, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, groupId, actionId)) {

            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, long groupId, String actionId) {

        return contains(permissionChecker, RESOURCE_NAME, PortletKeys.NEWSLETTER_MANAGER, groupId, actionId);
    }
    
    @Override
    public Boolean checkResource(PermissionChecker permissionChecker, long classPK, String actionId) {
        return contains(permissionChecker, classPK, actionId);
    }
}
