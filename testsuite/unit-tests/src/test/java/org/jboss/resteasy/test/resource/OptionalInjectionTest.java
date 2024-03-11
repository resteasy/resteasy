package org.jboss.resteasy.test.resource;

import java.io.ByteArrayInputStream;

import jakarta.ws.rs.NotFoundException;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.NotSupportedOpitionalPathParamResource;
import org.jboss.resteasy.test.resource.resource.OptionalResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests to make sure that Optional<T> types are injectable
 */
public class OptionalInjectionTest {

    ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
    MockHttpResponse resp = new MockHttpResponse();

    @BeforeEach
    public void setup() {
        registry.addPerRequestResource(OptionalResource.class);
    }

    @Test
    public void testOptionalStringAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/string");
        Assertions.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalStringPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/string?valueQ1=88");
        Assertions.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/holder");
        Assertions.assertEquals("none", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalHolderPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/holder?valueQ2=88");
        Assertions.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalLongAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/long", false);
        req.addFormHeader("notvalue", "badness");
        req.setInputStream(new ByteArrayInputStream(new byte[0]));

        Assertions.assertEquals("42", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testMatrixParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/matrix", false);
        Assertions.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testMatrixParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/matrix;valueM=24", false);
        Assertions.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testPathParamNotSupportedAndBehavedSameAsBeforeAsDiscussedInEAP7_1248() throws Exception {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    registry.addPerRequestResource(NotSupportedOpitionalPathParamResource.class);
                });
        Assertions.assertTrue(thrown instanceof RuntimeException);
    }

    @Test
    public void testPathParamNeverAbsentThrowsException() throws Exception {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    MockHttpRequest httpRequest = createMockHttpRequest("/optional/path/");
                    registry.getResourceInvoker(httpRequest)
                            .invoke(httpRequest, resp)
                            .getEntity();
                });
        Assertions.assertTrue(thrown instanceof NotFoundException);
    }

    @Test
    public void testHeaderParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/header");
        Assertions.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testHeaderParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/header");
        httpRequest.header("valueH", "24");
        Assertions.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testCookieParamAbsent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/cookie");
        Assertions.assertEquals("42", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testCookieParamPresent() throws Exception {
        MockHttpRequest httpRequest = createMockHttpRequest("/optional/cookie");
        httpRequest.cookie("valueC", "24");
        Assertions.assertEquals("24", registry
                .getResourceInvoker(httpRequest)
                .invoke(httpRequest, resp)
                .getEntity());
    }

    @Test
    public void testOptionalLongPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/long", false);
        req.addFormHeader("valueF", "88");

        Assertions.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/int");

        Assertions.assertEquals("424242", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalIntPresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/int?valueQ4=88");

        Assertions.assertEquals("88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoubleAbsent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/double");

        Assertions.assertEquals("4242.0", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    @Test
    public void testOptionalDoublePresent() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/optional/double?valueQ3=88.88");

        Assertions.assertEquals("88.88", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    /**
     * Builds an instance of {@code MockHttpRequest} for {@code GET} calls, properly configured to make
     * sure that all the {@code FormParam} are properly injected in all test cases otherwise we
     * end up with a {@code NullPointerException}.
     *
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
     *
     * @param uri   the uri of the endpoint to test.
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
