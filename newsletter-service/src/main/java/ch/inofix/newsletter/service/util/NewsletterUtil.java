package ch.inofix.newsletter.service.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.MailingLocalServiceUtil;
import ch.inofix.newsletter.service.NewsletterLocalServiceUtil;
import ch.inofix.newsletter.service.SubscriberLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-02 10:14
 * @modified 2017-09-02 10:14
 * @version 1.0.0
 *
 */
public class NewsletterUtil {

    public static List<Mailing> getMailings(Hits hits) {

        List<Document> documents = ListUtil.toList(hits.getDocs());

        List<Mailing> mailings = new ArrayList<Mailing>();

        for (Document document : documents) {
            try {
                long mailingId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

                Mailing mailing = MailingLocalServiceUtil.getMailing(mailingId);
                mailings.add(mailing);

            } catch (Exception e) {

                if (_log.isErrorEnabled()) {
                    _log.error(e.getMessage());
                }
            }
        }

        return mailings;
    }

    public static List<Newsletter> getNewsletters(Hits hits) {

        List<Document> documents = ListUtil.toList(hits.getDocs());

        List<Newsletter> newsletters = new ArrayList<Newsletter>();

        for (Document document : documents) {
            try {
                long newsletterId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

                Newsletter newsletter = NewsletterLocalServiceUtil.getNewsletter(newsletterId);
                newsletters.add(newsletter);

            } catch (Exception e) {

                if (_log.isErrorEnabled()) {
                    _log.error(e.getMessage());
                }
            }
        }

        return newsletters;
    }

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

    private static final Log _log = LogFactoryUtil.getLog(NewsletterUtil.class.getName());

}
