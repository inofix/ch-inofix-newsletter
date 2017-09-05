package ch.inofix.newsletter.service.permission;

import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.service.MailingLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 *
 * @author Christian Berndt
 * @created 2016-10-10 17:37
 * @modified 2017-03-10 16:34
 * @version 1.0.1
 *
 */
public class MailingPermission {

    /**
     *
     * @param permissionChecker
     * @param mailingId
     * @param actionId
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static void check(PermissionChecker permissionChecker, long mailingId, String actionId)
            throws PortalException, SystemException {

        if (!contains(permissionChecker, mailingId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, Mailing mailing, String actionId) {

        if (permissionChecker.hasOwnerPermission(mailing.getCompanyId(), Mailing.class.getName(),
                mailing.getMailingId(), mailing.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(mailing.getGroupId(), Mailing.class.getName(),
                String.valueOf(mailing.getMailingId()), actionId);
    }

    /**
     *
     * @param permissionChecker
     * @param mailingId
     * @param actionId
     * @return
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static boolean contains(PermissionChecker permissionChecker, long mailingId, String actionId)
            throws PortalException, SystemException {

        Mailing mailing = MailingLocalServiceUtil.getMailing(mailingId);

        if (permissionChecker.hasOwnerPermission(mailing.getCompanyId(), Mailing.class.getName(),
                mailing.getMailingId(), mailing.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(mailing.getGroupId(), Mailing.class.getName(), mailing.getMailingId(),
                actionId);

    }

}
