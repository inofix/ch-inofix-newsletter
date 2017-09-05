package ch.inofix.newsletter.service.impl;

import java.util.LinkedHashMap;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;

import aQute.bnd.annotation.ProviderType;

import ch.inofix.newsletter.service.base.SubscriberServiceBaseImpl;

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
 * @modified 2017-09-02 11:06
 * @version 1.0.0
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
}