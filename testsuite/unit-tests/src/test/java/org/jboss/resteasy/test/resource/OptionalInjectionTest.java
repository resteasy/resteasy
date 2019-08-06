package org.jboss.resteasy.test.resource;

import java.io.ByteArrayInputStream;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.OptionalResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests to make sure that Optional<T> types are injectable
 */
public class OptionalInjectionTest {

    ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
    MockHttpResponse resp = new MockHttpResponse();

    @Before
    public void setup() {
        registry.addPerRequestResource(OptionalResource.class);
    }

    @Test
    public void testOptionalStringAbsent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/string");
        Assert.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp).getEntity());

    }

    @Test
    public void testOptionalStringPresent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/string?value=88");
        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderAbsent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/holder");
        Assert.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderPresent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/holder?value=88");
        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalLongAbsent() throws Exception {
        MockHttpRequest req = MockHttpRequest.post("/optional/long");
        req.addFormHeader("notvalue", "badness");
        req.setInputStream(new ByteArrayInputStream(new byte[0]));

        Assert.assertEquals("42", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalLongPresent() throws Exception {
        MockHttpRequest req = MockHttpRequest.post("/optional/long");
        req.addFormHeader("value", "88");

        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntAbsent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/int");

        Assert.assertEquals("424242", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntPresent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/int?value=88");

        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoubleAbsent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/double");

        Assert.assertEquals("4242.0", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoublePresent() throws Exception {
        MockHttpRequest req = MockHttpRequest.get("/optional/double?value=88.88");

        Assert.assertEquals("88.88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }
}
