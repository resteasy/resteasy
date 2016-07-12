package org.jboss.resteasy.test.injection;

import org.jboss.resteasy.cdi.Utils;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionRootResource;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionSampleProvider;
import org.jboss.resteasy.test.injection.resource.JaxrsComponentDetectionSubresource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        assertTrue(WRONG_RESOURCE, Utils.isJaxrsResource(JaxrsComponentDetectionRootResource.class));
        assertTrue(WRONG_COMPONENT, Utils.isJaxrsComponent(JaxrsComponentDetectionRootResource.class));
    }

    /**
     * @tpTestDetails Check subresource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresource() {
        assertTrue(WRONG_RESOURCE, Utils.isJaxrsResource(JaxrsComponentDetectionSubresource.class));
        assertTrue(WRONG_COMPONENT, Utils.isJaxrsComponent(JaxrsComponentDetectionSubresource.class));
    }

    /**
     * @tpTestDetails Check application subclass.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApplicationSubclass() {
        assertTrue(WRONG_COMPONENT, Utils.isJaxrsComponent(JaxrsComponentDetectionSubresource.class));
    }

    /**
     * @tpTestDetails Check provider.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProvider() {
        assertTrue(WRONG_COMPONENT, Utils.isJaxrsComponent(JaxrsComponentDetectionSampleProvider.class));
    }
}
