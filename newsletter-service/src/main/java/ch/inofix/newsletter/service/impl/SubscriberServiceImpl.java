package ch.inofix.newsletter.service.impl;

import java.util.LinkedHashMap;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.newsletter.constants.NewsletterActionKeys;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.security.ActionKeys;
import ch.inofix.newsletter.service.base.SubscriberServiceBaseImpl;
import ch.inofix.newsletter.service.permission.NewsletterPortletPermission;
import ch.inofix.newsletter.service.permission.SubscriberPermission;

/**
 * The implementation of the subscriber remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.newsletter.service.SubscriberService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt
 * @created 2017-09-02 11:06
 * @modified 2017-09-17 21:44
 * @version 1.0.1
 * @see SubscriberServiceBaseImpl
 * @see ch.inofix.newsletter.service.SubscriberServiceUtil
 */
@ProviderType
public class SubscriberServiceImpl extends SubscriberServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.newsletter.service.SubscriberServiceUtil} to access the
     * subscriber remote service.
     */

    @Override
    public Subscriber addSubscriber(String email, String firstname, String gender, String lastname, String middlename,
            long newsletterId, String salutation, String title, int status, ServiceContext serviceContext)
            throws PortalException {

        NewsletterPortletPermission.check(getPermissionChecker(), serviceContext.getScopeGroupId(),
                NewsletterActionKeys.ADD_SUBSCRIBER);

        return subscriberLocalService.addSubscriber(getUserId(), email, firstname, gender, lastname, middlename,
                newsletterId, salutation, title, serviceContext);

    }

    @Override
    public Subscriber createSubscriber() throws PortalException {

        // Create an empty subscriber - no permission check required
        return subscriberLocalService.createSubscriber(0);

    }

    @Override
    public Subscriber deleteSubscriber(long subscriberId) throws PortalException {

        SubscriberPermission.check(getPermissionChecker(), subscriberId, ActionKeys.DELETE);

        Subscriber subscriber = subscriberLocalService.deleteSubscriber(subscriberId);

        return subscriber;

    }

    @Override
    public Subscriber getSubscriber(long subscriberId) throws PortalException {

        SubscriberPermission.check(getPermissionChecker(), subscriberId, ActionKeys.VIEW);

        return subscriberLocalService.getSubscriber(subscriberId);

    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String keywords, int start, int end, Sort sort)
            throws PortalException {

        return subscriberLocalService.search(userId, groupId, ownerUserId, keywords, start, end, sort);
    }

    @Override
    public Hits search(long userId, long groupId, long ownerUserId, String title, String description, int status,
            LinkedHashMap<String, Object> params, boolean andSearch, int start, int end, Sort sort)
            throws PortalException {

        return subscriberLocalService.search(userId, groupId, ownerUserId, title, description, status, params,
                andSearch, start, end, sort);

    }

    @Override
    public Subscriber updateSubscriber(long subscriberId, String email, String firstname, String gender,
            String lastname, String middlename, long newsletterId, String salutation, String title, int status,
            ServiceContext serviceContext) throws PortalException {

        SubscriberPermission.check(getPermissionChecker(), subscriberId, ActionKeys.UPDATE);

        return subscriberLocalService.updateSubscriber(subscriberId, getUserId(), email, firstname, gender, lastname,
                middlename, newsletterId, salutation, title, serviceContext);
    }
}