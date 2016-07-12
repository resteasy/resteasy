package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.SegmentLocatorComplex;
import org.jboss.resteasy.test.resource.resource.SegmentNullResource;
import org.jboss.resteasy.test.resource.resource.SegmentResource;
import org.jboss.resteasy.test.resource.resource.SegmentResourceSwitch;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.NotAllowedException;
import java.net.URISyntaxException;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests to make sure that standard segment mapping work correctly, especially
 *                    regexes that contain "\"
 * @tpSince RESTEasy 3.0.16
 */
public class SegmentTest {

    /**
     * @tpTestDetails Basic segment check
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBasic() throws URISyntaxException {
        ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
                .getInstance());
        registry.addSingletonResource(new SegmentNullResource());
        assertMatchRoot(registry, "/", "doNothing", SegmentNullResource.class);
        assertMatchRoot(registry, "/child", "childDoNothing", SegmentNullResource.class);
        assertMatchRoot(registry, "/child/foo", "childWithName", SegmentNullResource.class);
        assertMatchRoot(registry, "/child/1", "childWithId", SegmentNullResource.class);
        assertMatchRoot(registry, "/child1/1", "child1WithId", SegmentNullResource.class);
    }

    /**
     * @tpTestDetails Check default option for segment
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDefaultOptions() throws URISyntaxException {
        ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
                .getInstance());
        registry.addPerRequestResource(SegmentResource.class);
        try {
            ResourceInvoker invoker = registry.getResourceInvoker(MockHttpRequest.options("/resource/sub"));
        } catch (DefaultOptionsMethodException e) {
        }
        try {
            ResourceInvoker invoker = registry.getResourceInvoker(MockHttpRequest.put("/resource/sub"));
        } catch (NotAllowedException e) {
        }
    }

    /**
     * @tpTestDetails Check locator option
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocatorOptions() throws URISyntaxException {
        ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
                .getInstance());
        registry.addPerRequestResource(SegmentResourceSwitch.class);
        ResourceLocatorInvoker invoker = (ResourceLocatorInvoker) registry.getResourceInvoker(MockHttpRequest.options("/resource/sub"));
        Assert.assertNotNull("Wrong ResourceLocatorInvoker response", invoker);
        Assert.assertEquals("Wrong ResourceLocatorInvoker response", invoker.getMethod().getName(), "locator");
    }

    /**
     * @tpTestDetails Check complex locator
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocator3() throws URISyntaxException {
        ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
                .getInstance());
        registry.addPerRequestResource(SegmentLocatorComplex.class);
        ResourceLocatorInvoker invoker = (ResourceLocatorInvoker) registry.getResourceInvoker(MockHttpRequest.get("/locator/responseok/responseok"));
        Assert.assertNotNull("Wrong ResourceLocatorInvoker response", invoker);
        Assert.assertEquals("Wrong ResourceLocatorInvoker response", invoker.getMethod().getName(), "responseOk");
    }

    private void assertMatchRoot(ResourceMethodRegistry registry, final String url, final String methodName,
                                 final Class<?> clazz) throws URISyntaxException {
        ResourceMethodInvoker matchRoot = getResourceMethod(url, registry);
        Assert.assertEquals("Wrong ResourceLocatorInvoker response", clazz, matchRoot.getResourceClass());
        Assert.assertEquals("Wrong ResourceLocatorInvoker response", methodName, matchRoot.getMethod().getName());
    }

    private ResourceMethodInvoker getResourceMethod(String url, ResourceMethodRegistry registry)
            throws URISyntaxException {
        return (ResourceMethodInvoker) registry.getResourceInvoker(MockHttpRequest.get(url));
    }

}
