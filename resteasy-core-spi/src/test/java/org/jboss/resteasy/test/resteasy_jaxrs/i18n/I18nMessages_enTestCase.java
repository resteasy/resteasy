package org.jboss.resteasy.test.resteasy_jaxrs.i18n;

import java.util.Locale;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 20, 2015
 */
public class I18nMessages_enTestCase extends TestMessagesAbstract {
    @Override
    protected Locale getLocale() {
        return new Locale("en");
    }
}
