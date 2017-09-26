package ch.inofix.newsletter.service.permission;

import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.SubscriberLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-26 21:23
 * @modified 2017-09-26 21:23
 * @version 1.0.2
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

    public static boolean contains(PermissionChecker permissionChecker, Subscriber subscriber, String actionId) {

        if (permissionChecker.hasOwnerPermission(subscriber.getCompanyId(), Subscriber.class.getName(),
                subscriber.getSubscriberId(), subscriber.getUserId(), actionId)) {

            return true;
        }

        return permissionChecker.hasPermission(subscriber.getGroupId(), Subscriber.class.getName(),
                String.valueOf(subscriber.getSubscriberId()), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, long subscriberId, String actionId) {

        Subscriber subscriber;
        try {
            subscriber = SubscriberLocalServiceUtil.getSubscriber(subscriberId);
            return contains(permissionChecker, subscriber, actionId);
        } catch (PortalException e) {
            _log.error(e);
        }

        return false;
    }

    private static final Log _log = LogFactoryUtil.getLog(SubscriberPermission.class.getName());

}
