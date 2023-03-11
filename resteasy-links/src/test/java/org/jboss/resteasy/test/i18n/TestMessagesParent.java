package org.jboss.resteasy.test.i18n;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 20, 2015
 */
public abstract class TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesParent.class);
    protected static Locale savedLocale;

    protected Properties properties = new Properties();

    @BeforeClass
    public static void beforeClass() {
        savedLocale = Locale.getDefault();
    }

    @AfterClass
    public static void afterClass() {
        Locale.setDefault(savedLocale);
        LOG.info("Reset default locale to: " + savedLocale);
    }

    public boolean before(Locale locale, String filename) throws Exception {
        LOG.info("default locale: " + Locale.getDefault());
        Locale.setDefault(locale);
        LOG.info("Set default locale to: " + locale);
        LOG.info("Messages file: " + filename);
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            return false;
        }
        properties.load(is);
        LOG.info("properties.size(): " + properties.size());
        return getExpectedNumberOfMethods() == properties.size();
    }

    protected String getExpected(String id, String message, Object... args) {
        String expected = "RESTEASY" + id + ": "
                + String.format(replacePositionalSpecifiers(String.class.cast(properties.get(message))), args);
        LOG.info("expected: " + expected);
        return expected;
    }

    protected String replacePositionalSpecifiers(String s) {
        //      LOG.info("before: " + s);
        int pos0 = s.indexOf("{0}");
        if (pos0 > -1) {
            s = s.substring(0, pos0) + "%1$s" + (pos0 + 3 >= s.length() ? "" : s.substring(pos0 + 3));
        }
        int pos1 = s.indexOf("{1}");
        if (pos1 > -1) {
            s = s.substring(0, pos1) + "%2$s" + (pos1 + 3 >= s.length() ? "" : s.substring(pos1 + 3));
        }
        int pos2 = s.indexOf("{2}");
        if (pos2 > -1) {
            s = s.substring(0, pos2) + "%3$s" + (pos2 + 3 >= s.length() ? "" : s.substring(pos2 + 3));
        }
        //      LOG.info("after: " + s);
        return s;
    }

    protected abstract int getExpectedNumberOfMethods();

    protected abstract Locale getLocale();
}
