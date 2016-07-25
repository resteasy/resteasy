package org.jboss.resteasy.test.util;

import org.jboss.resteasy.spi.touri.ObjectToURI;
import org.jboss.resteasy.spi.touri.URIResolver;
import org.jboss.resteasy.test.util.resource.ObjectToURICustomURIableObject;
import org.jboss.resteasy.test.util.resource.ObjectToURIMappedByObject;
import org.jboss.resteasy.test.util.resource.ObjectToURITemplateObject;
import org.jboss.resteasy.test.util.resource.ObjectToURIableObject;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(ERROR_MSG, "/foo/123", instance.resolveURI(new ObjectToURITemplateObject(123)));
        Assert.assertEquals(ERROR_MSG, "/my-url", instance.resolveURI(new ObjectToURIableObject()));
        Assert.assertEquals(ERROR_MSG, "/foo/123", instance.resolveURI(new ObjectToURIMappedByObject(123)));
    }

    /**
     * @tpTestDetails Check custom resolvers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomResolver() {
        ObjectToURI instance = ObjectToURI.getInstance();
        ObjectToURICustomURIableObject custom = new ObjectToURICustomURIableObject();
        Assert.assertEquals(ERROR_MSG, "/my-url", instance.resolveURI(custom));

        instance.registerURIResolver(new URIResolver() {
            public boolean handles(Class<?> type) {
                return type == ObjectToURICustomURIableObject.class;
            }

            public String resolveURI(Object object) {
                return "/some-other-uri";
            }
        });

        Assert.assertEquals(ERROR_MSG, "/some-other-uri", instance.resolveURI(custom));
    }
}
