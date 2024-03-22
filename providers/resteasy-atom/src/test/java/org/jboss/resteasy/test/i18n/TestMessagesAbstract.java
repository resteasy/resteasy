package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.resteasy_atom.i18n.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Sep 27, 2014
 */
public abstract class TestMessagesAbstract extends TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
    protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);

    @Test
    public void testLocale() throws Exception {
        Locale locale = getLocale();
        String filename = "org/jboss/resteasy/plugins/providers/resteasy_atom/i18n/Messages.i18n_" + locale.toString()
                + ".properties";
        if (!before(locale, filename)) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }

        Assertions.assertEquals(getExpected(BASE + "00", "consructorMustBeCalled"), Messages.MESSAGES.consructorMustBeCalled());
        Assertions.assertEquals(getExpected(BASE + "05", "unableToFindJAXBContext", "xx/yy"),
                Messages.MESSAGES.unableToFindJAXBContext(new MediaType("xx", "yy")));
        Assertions.assertEquals(getExpected(BASE + "10", "unableToMarshal", "xx/yy"),
                Messages.MESSAGES.unableToMarshal(new MediaType("xx", "yy")));
        Assertions.assertEquals(getExpected(BASE + "15", "unableToUnmarshal", "xx/yy"),
                Messages.MESSAGES.unableToUnmarshal(new MediaType("xx", "yy")));
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        return Messages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
