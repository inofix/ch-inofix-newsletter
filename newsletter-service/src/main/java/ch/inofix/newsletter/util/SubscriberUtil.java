package ch.inofix.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.SubscriberLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-17 21:15
 * @modified 2017-09-17 21:15
 * @version 1.0.0
 *
 */
public class SubscriberUtil {

    public static List<Subscriber> getSubscribers(Hits hits) {

        List<Document> documents = ListUtil.toList(hits.getDocs());

        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        for (Document document : documents) {
            try {
                long subscriberId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

                Subscriber subscriber = SubscriberLocalServiceUtil.getSubscriber(subscriberId);
                subscribers.add(subscriber);

            } catch (Exception e) {

                if (_log.isErrorEnabled()) {
                    _log.error(e.getMessage());
                }
            }
        }

        return subscribers;
    }

    private static final Log _log = LogFactoryUtil.getLog(SubscriberUtil.class.getName());

}
