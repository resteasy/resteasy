package org.jboss.resteasy.test.injection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.resteasy.cdi.Utils;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionRootResource;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionSampleProvider;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionSubresource;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Injection tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.cdi.Utils class.
 * @tpSince RESTEasy 3.0.16
 */
public class JaxrsComponentDetectionTest {
    private static final String WRONG_RESOURCE = "Method isJaxrsResource works incorrectly";
    private static final String WRONG_COMPONENT = "Method isJaxrsComponent works incorrectly";

    /**
     * @tpTestDetails Check root resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRootResource() {
        assertTrue(Utils.isJaxrsResource(JaxrsComponentDetectionRootResource.class),
                WRONG_RESOURCE);
        assertTrue(Utils.isJaxrsComponent(JaxrsComponentDetectionRootResource.class),
                WRONG_COMPONENT);
    }

    /**
     * @tpTestDetails Check subresource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresource() {
        assertTrue(Utils.isJaxrsResource(JaxrsComponentDetectionSubresource.class),
                WRONG_RESOURCE);
        assertTrue(Utils.isJaxrsComponent(JaxrsComponentDetectionSubresource.class),
                WRONG_COMPONENT);
    }

    /**
     * @tpTestDetails Check application subclass.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApplicationSubclass() {
        assertTrue(Utils.isJaxrsComponent(JaxrsComponentDetectionSubresource.class),
                WRONG_COMPONENT);
    }

    /**
     * @tpTestDetails Check provider.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProvider() {
        assertTrue(Utils.isJaxrsComponent(JaxrsComponentDetectionSampleProvider.class),
                WRONG_COMPONENT);
    }
}
