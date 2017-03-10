package ch.inofix.newsletter.service.permission;

import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.SubscriberLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 *
 * @author Christian Berndt
 * @created 2016-10-20 18:43
 * @modified 2017-03-10 16:38
 * @version 1.0.1
 *
 */
public class SubscriberPermission {

    /**
     *
     * @param permissionChecker
     * @param subscriberId
     * @param actionId
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static void check(PermissionChecker permissionChecker, long subscriberId, String actionId)
            throws PortalException, SystemException {

        if (!contains(permissionChecker, subscriberId, actionId)) {
            throw new PrincipalException();
        }
    }

    /**
     *
     * @param permissionChecker
     * @param subscriberId
     * @param actionId
     * @return
     * @since 1.0.0
     * @throws PortalException
     * @throws SystemException
     */
    public static boolean contains(PermissionChecker permissionChecker, long subscriberId, String actionId)
            throws PortalException, SystemException {

        Subscriber subscriber = SubscriberLocalServiceUtil.getSubscriber(subscriberId);

        if (permissionChecker.hasOwnerPermission(subscriber.getCompanyId(), Subscriber.class.getName(),
                subscriber.getSubscriberId(), subscriber.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(subscriber.getGroupId(), Subscriber.class.getName(),
                subscriber.getSubscriberId(), actionId);

    }
}
