package org.jboss.resteasy.test.i18n;

import java.lang.reflect.TypeVariable;
import java.util.Locale;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 24, 2015
 */
public abstract class TestMessagesAbstract extends TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
    protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
    protected TypeVariable<?> var;
    protected MediaType mediaType = new MediaType("xx", "yy", "zz");

    @Test
    public void testLocale() throws Exception {
        Locale locale = getLocale();
        String filename = "org/jboss/resteasy/client/jaxrs/i18n/Messages.i18n_" + locale.toString() + ".properties";
        if (!before(locale, filename)) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }

        Assertions.assertEquals(getExpected(BASE + "00", "canOnlySetOneLinkHeaderParam", "abc", "xyz"),
                Messages.MESSAGES.canOnlySetOneLinkHeaderParam("abc", "xyz"));
        Assertions.assertEquals(getExpected(BASE + "20", "couldNotCreateURL", "xx", "yy", "zz"),
                Messages.MESSAGES.couldNotCreateURL("xx", "yy", "zz"));
        Assertions.assertEquals(getExpected(BASE + "40", "doesNotSpecifyTypeParameter", var),
                Messages.MESSAGES.doesNotSpecifyTypeParameter(var));
        Assertions.assertEquals(getExpected(BASE + "60", "failedToBufferAbortedResponseNoWriter", mediaType, "class"),
                Messages.MESSAGES.failedToBufferAbortedResponseNoWriter(mediaType, "class"));
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        return Messages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
