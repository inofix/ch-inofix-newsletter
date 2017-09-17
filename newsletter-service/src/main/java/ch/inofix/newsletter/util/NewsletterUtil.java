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

import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-17 21:00
 * @modified 2017-09-17 21:00
 * @version 1.0.0
 *
 */
public class NewsletterUtil {

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

    private static final Log _log = LogFactoryUtil.getLog(NewsletterUtil.class.getName());

}
