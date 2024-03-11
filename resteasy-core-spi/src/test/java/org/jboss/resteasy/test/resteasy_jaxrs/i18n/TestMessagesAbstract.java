package org.jboss.resteasy.test.resteasy_jaxrs.i18n;

import java.net.URI;
import java.util.Locale;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 20, 2015
 */
public abstract class TestMessagesAbstract extends TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
    protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 3);

    @Test
    public void testLocale() throws Exception {
        Locale locale = getLocale();
        String filename = "org/jboss/resteasy/resteasy_jaxrs/i18n/Messages.i18n_" + locale.toString() + ".properties";
        if (!before(locale, filename)) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }
        Assertions.assertEquals(getExpected(BASE + "000", "alwaysMarkedAtIndex0"), Messages.MESSAGES.alwaysMarkedAtIndex0());
        Assertions.assertEquals(getExpected(BASE + "005", "ambiguousInheritedAnnotations", getClass().getMethod("testLocale")),
                Messages.MESSAGES.ambiguousInheritedAnnotations(getClass().getMethod("testLocale")));
        Assertions.assertEquals(getExpected(BASE + "025", "badBase64Character", 17, 29),
                Messages.MESSAGES.badBase64Character(17, 29));
        Assertions.assertEquals(getExpected(BASE + "045", "cacheControlMaxAgeHeader", "header"),
                Messages.MESSAGES.cacheControlMaxAgeHeader("header"));
        Assertions.assertEquals(getExpected(BASE + "085", "cannotHaveOffset", 17, 29, 37),
                Messages.MESSAGES.cannotHaveOffset(17, 29, 37));
        Assertions.assertEquals(getExpected(BASE + "135", "classMustBeAnnotatedWithPath"),
                Messages.MESSAGES.classMustBeAnnotatedWithPath());
        Assertions.assertEquals(
                getExpected(BASE + "145", "clientResponseFailureMediaType", new MediaType("xx", "yy"), getClass()),
                Messages.MESSAGES.clientResponseFailureMediaType(new MediaType("xx", "yy"), getClass()));
        Assertions.assertEquals(
                getExpected(BASE + "150", "clientResponseFailureStatus", Status.GONE.getStatusCode(), Status.GONE),
                Messages.MESSAGES.clientResponseFailureStatus(Status.GONE.getStatusCode(), Status.GONE));
        Assertions.assertEquals(
                getExpected(BASE + "200", "couldNotFindMessageBodyReader", getClass().getGenericSuperclass(),
                        new MediaType("xx", "yy")),
                Messages.MESSAGES.couldNotFindMessageBodyReader(getClass().getGenericSuperclass(), new MediaType("xx", "yy")));
        Assertions.assertEquals(getExpected(BASE + "210", "couldNotFindResourceForFullPath", new URI("http://localhost")),
                Messages.MESSAGES.couldNotFindResourceForFullPath(new URI("http://localhost")));
        Assertions.assertEquals(getExpected(BASE + "285", "entityAlreadyRead", getClass()),
                Messages.MESSAGES.entityAlreadyRead(getClass()));
        Assertions.assertEquals(getExpected(BASE + "345", "fileTooBig", 345), Messages.MESSAGES.fileTooBig(345));
        Assertions.assertEquals(getExpected(BASE + "365", "illegalHexadecimalCharacter", 'x', 3),
                Messages.MESSAGES.illegalHexadecimalCharacter('x', 3));
        Assertions.assertEquals(getExpected(BASE + "395", "illegalToInjectMessageBody", getClass().getMethod("testLocale")),
                Messages.MESSAGES.illegalToInjectMessageBody(getClass().getMethod("testLocale")));
        Assertions.assertEquals(getExpected(BASE + "450", "interceptorClassMustBeAnnotatedWithClass", getClass()),
                Messages.MESSAGES.interceptorClassMustBeAnnotatedWithClass(getClass()));
        Assertions.assertEquals(getExpected(BASE + "560", "methodNotAnnotatedWithPath"),
                Messages.MESSAGES.methodNotAnnotatedWithPath());
        Assertions.assertEquals(getExpected(BASE + "580", "mustSetLinkHeaderRelOrTitle", "class", "method"),
                Messages.MESSAGES.mustSetLinkHeaderRelOrTitle("class", "method"));
        Assertions.assertEquals(getExpected(BASE + "730", "qValueCannotBeGreaterThan1", "x/y;q=1.1"),
                Messages.MESSAGES.qValueCannotBeGreaterThan1("x/y;q=1.1"));
        Assertions.assertEquals(getExpected(BASE + "750", "requestMediaTypeNotUrlencoded"),
                Messages.MESSAGES.requestMediaTypeNotUrlencoded());
        Assertions.assertEquals(getExpected(BASE + "975", "unableToUnmarshalResponse", "abc", new Exception("unable")),
                Messages.MESSAGES.unableToUnmarshalResponse("abc"));
        Assertions.assertEquals(getExpected("004000", "unsupportedCollectionType", getClass()),
                Messages.MESSAGES.unsupportedCollectionType(getClass()));
        Assertions.assertEquals(getExpected("004070", "wrongPassword", "user"), Messages.MESSAGES.wrongPassword("user"));
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        return Messages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
