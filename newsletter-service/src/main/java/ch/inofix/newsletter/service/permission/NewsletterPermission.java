package ch.inofix.newsletter.service.permission;

import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 *
 * @author Christian Berndt
 * @created 2016-10-08 00:32
 * @modified 2017-09-16 00:05
 * @version 1.0.2
 *
 */
public class NewsletterPermission {

    /**
     *
     * @param permissionChecker
     * @param newsletterId
     * @param actionId
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static void check(PermissionChecker permissionChecker,
            long newsletterId, String actionId) throws PortalException,
            SystemException {

        if (!contains(permissionChecker, newsletterId, actionId)) {
            throw new PrincipalException();
        }
    }

    /**
     *
     * @param permissionChecker
     * @param newsletterId
     * @param actionId
     * @return
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static boolean contains(PermissionChecker permissionChecker,
            long newsletterId, String actionId) throws PortalException,
            SystemException {

        Newsletter newsletter = NewsletterLocalServiceUtil
                .getNewsletter(newsletterId);

        if (permissionChecker.hasOwnerPermission(newsletter.getCompanyId(),
                Newsletter.class.getName(), newsletter.getNewsletterId(),
                newsletter.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(newsletter.getGroupId(),
                Newsletter.class.getName(), newsletter.getNewsletterId(),
                actionId);

    }
    
    public static boolean contains(PermissionChecker permissionChecker,
            Newsletter newsletter, String actionId) throws PortalException,
            SystemException {

        if (permissionChecker.hasOwnerPermission(newsletter.getCompanyId(),
                Newsletter.class.getName(), newsletter.getNewsletterId(),
                newsletter.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(newsletter.getGroupId(),
                Newsletter.class.getName(), newsletter.getNewsletterId(),
                actionId);

    }
}
