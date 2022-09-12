package org.jboss.resteasy.test.resource;

import java.io.ByteArrayInputStream;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.NotSupportedOpitionalPathParamResource;
import org.jboss.resteasy.test.resource.resource.OptionalResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.NotFoundException;

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
        MockHttpRequest req = createMockHttpRequest("/optional/string");
        Assert.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }


    @Test
    public void testOptionalStringPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/string?valueQ1=88");
        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/holder");
        Assert.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/holder?valueQ2=88");
        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalLongAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/long", false);
        req.addFormHeader("notvalue", "badness");
        req.setInputStream(new ByteArrayInputStream(new byte[0]));

        Assert.assertEquals("42", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testMatrixParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/matrix", false);
        Assert.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testMatrixParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/matrix;valueM=24", false);
        Assert.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test(expected = RuntimeException.class)
    public void testPathParamNotSupportedAndBehavedSameAsBeforeAsDiscussedInEAP7_1248() throws Exception {
        registry.addPerRequestResource(NotSupportedOpitionalPathParamResource.class);
    }

    @Test(expected = NotFoundException.class)
    public void testPathParamNeverAbsentThrowsException() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/path/");
        registry.getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity();
    }

    @Test
    public void testHeaderParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/header");
        Assert.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }


    @Test
    public void testHeaderParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/header");
        httpRequest.header("valueH", "24");
        Assert.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testCookieParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/cookie");
        Assert.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testCookieParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/cookie");
        httpRequest.cookie("valueC", "24");
        Assert.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }


    @Test
    public void testOptionalLongPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/long", false);
        req.addFormHeader("valueF", "88");

        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/int");

        Assert.assertEquals("424242", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/int?valueQ4=88");

        Assert.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoubleAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/double");

        Assert.assertEquals("4242.0", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoublePresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/double?valueQ3=88.88");

        Assert.assertEquals("88.88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    /**
     * Builds an instance of {@code MockHttpRequest} for {@code GET} calls, properly configured to make
     * sure that all the {@code FormParam} are properly injected in all test cases otherwise we
     * end up with a {@code NullPointerException}.
     * @param uri the uri of the endpoint to test.
     * @return an instance of {@code MockHttpRequest} properly configured.
     * @throws Exception in case the provided uri is not properly formed.
     */
    private static MockHttpRequest createMockHttpRequest(String uri) throws Exception {
        return createMockHttpRequest(uri, true);
    }

    /**
     * Builds an instance of {@code MockHttpRequest} properly configured to make sure that all
     * the {@code FormParam} are properly injected in all test cases otherwise we end up with
     * a {@code NullPointerException}.
     * @param uri the uri of the endpoint to test.
     * @param isGet the flag indicating whether a GET call is expected otherwise a POST call will be
     *              performed.
     * @return an instance of {@code MockHttpRequest} properly configured.
     * @throws Exception in case the provided uri is not properly formed.
     */
    private static MockHttpRequest createMockHttpRequest(String uri, boolean isGet) throws Exception {
        MockHttpRequest req = isGet ? MockHttpRequest.get(uri) : MockHttpRequest.post(uri);
        req.addFormHeader("foo", "bar");
        return req;
    }
}
