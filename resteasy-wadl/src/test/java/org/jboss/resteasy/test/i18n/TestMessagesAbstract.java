package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.jboss.logging.Logger;
import org.jboss.resteasy.wadl.i18n.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright January 6, 2016
 */
public abstract class TestMessagesAbstract extends TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
    protected String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);

    @Test
    public void testLocale() throws Exception {
        Locale locale = getLocale();
        String filename = "org/jboss/resteasy/wadl/i18n/Messages.i18n_" + locale.toString() + ".properties";
        if (!before(locale, filename)) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }

        Assertions.assertEquals(getExpected(BASE + "00", "impossibleToGenerateWADL", "class", "method"),
                Messages.MESSAGES.impossibleToGenerateWADL("class", "method"));
        Assertions.assertEquals(getExpected(BASE + "15", "overridingConsumesAnnotation"),
                Messages.MESSAGES.overridingConsumesAnnotation());
        Assertions.assertEquals(getExpected(BASE + "35", "servingPathInfo", "path"), Messages.MESSAGES.servingPathInfo("path"));
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        return Messages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
