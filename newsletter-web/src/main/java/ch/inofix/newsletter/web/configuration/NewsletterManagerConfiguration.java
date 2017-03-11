package ch.inofix.newsletter.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * 
 * @author Christian Berndt
 * @created 2017-03-10 23:37
 * @modified 2017-03-10 23:37
 * @version 1.0.0
 *
 */
@Meta.OCD(id = "ch.inofix.newsletter.web.configuration.NewsletterManagerConfiguration", localization = "content/Language", name = "newsletter.configuration.name")
public interface NewsletterManagerConfiguration {

    @Meta.AD(deflt = "referenceId|type|label|author|title|year|modified", required = false)
    public String[] columns();
}
