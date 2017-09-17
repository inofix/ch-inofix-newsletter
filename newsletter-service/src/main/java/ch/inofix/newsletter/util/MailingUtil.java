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

import ch.inofix.newsletter.model.Mailing;
import ch.inofix.newsletter.service.MailingLocalServiceUtil;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-17 20:54
 * @modified 2017-09-17 20:54
 * @version 1.0.0
 *
 */
public class MailingUtil {

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

    private static final Log _log = LogFactoryUtil.getLog(MailingUtil.class.getName());

}
