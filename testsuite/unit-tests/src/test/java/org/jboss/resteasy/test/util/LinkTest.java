package org.jboss.resteasy.test.util;

import java.lang.reflect.Method;
import java.net.URI;

import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriBuilderException;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.util.resource.LinkResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.core.Link class.
 * @tpSince RESTEasy 3.0.16
 */
public class LinkTest {

    protected static final Logger logger = Logger.getLogger(LinkTest.class.getName());

    /**
     * @tpTestDetails Check relative path option
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRelativized() throws Exception {
        URI uri = new URI("a").relativize(new URI("a/d/e"));
        logger.info(uri);

        Link link = Link.fromUri("a/d/e")
                .rel("update").type("text/plain")
                .buildRelativized(new URI("a"));
        logger.info(link.toString());

        link = Link.fromUri("a/d/e")
                .rel("update").type("text/plain")
                .baseUri("http://localhost/")
                .buildRelativized(new URI("http://localhost/a"));
        logger.info(link.toString());

    }

    /**
     * @tpTestDetails Check fromMethod method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFromMethod() {
        for (Method m : LinkResource.class.getDeclaredMethods()) {
            // This condition is required, when test runs with jacoco, due to use of reflection.
            //See https://groups.google.com/forum/#!topic/jacoco/tuOwqU_Tv78 for details
            if (!m.isSynthetic()) {
                Link link = Link.fromMethod(LinkResource.class, m.getName()).build();
                String string = link.toString();
                logger.info("initial: " + string);
                Link fromValueOf = Link.valueOf(string);
                Assertions.assertEquals(string, fromValueOf.toString());
            }
        }
    }

    /**
     * @tpTestDetails Check wrong URI
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIllegalUri() {
        Link.Builder builder = Link.fromUri("http://:@");
        try {
            builder.build();
            Assertions.fail();
        } catch (UriBuilderException e) {
        }
    }

}
