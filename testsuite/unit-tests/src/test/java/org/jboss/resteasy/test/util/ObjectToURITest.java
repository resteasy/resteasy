package org.jboss.resteasy.test.util;

import org.jboss.resteasy.plugins.touri.ObjectToURI;
import org.jboss.resteasy.spi.touri.URIResolver;
import org.jboss.resteasy.test.util.resource.ObjectToURICustomURIableObject;
import org.jboss.resteasy.test.util.resource.ObjectToURIMappedByObject;
import org.jboss.resteasy.test.util.resource.ObjectToURITemplateObject;
import org.jboss.resteasy.test.util.resource.ObjectToURIableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.spi.touri.ObjectToRUI class.
 * @tpSince RESTEasy 3.0.16
 */
public class ObjectToURITest {
    private static final String ERROR_MSG = "Wrong conversion to URI";

    /**
     * @tpTestDetails Check default resolvers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDefaultResolvers() {
        ObjectToURI instance = ObjectToURI.getInstance();
        Assertions.assertEquals("/foo/123", instance.resolveURI(new ObjectToURITemplateObject(123)),
                ERROR_MSG);
        Assertions.assertEquals("/my-url", instance.resolveURI(new ObjectToURIableObject()),
                ERROR_MSG);
        Assertions.assertEquals("/foo/123", instance.resolveURI(new ObjectToURIMappedByObject(123)),
                ERROR_MSG);
    }

    /**
     * @tpTestDetails Check custom resolvers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomResolver() {
        ObjectToURI instance = ObjectToURI.getInstance();
        ObjectToURICustomURIableObject custom = new ObjectToURICustomURIableObject();
        Assertions.assertEquals("/my-url", instance.resolveURI(custom), ERROR_MSG);

        instance.registerURIResolver(new URIResolver() {
            public boolean handles(Class<?> type) {
                return type == ObjectToURICustomURIableObject.class;
            }

            public String resolveURI(Object object) {
                return "/some-other-uri";
            }
        });

        Assertions.assertEquals("/some-other-uri", instance.resolveURI(custom), ERROR_MSG);
    }
}
