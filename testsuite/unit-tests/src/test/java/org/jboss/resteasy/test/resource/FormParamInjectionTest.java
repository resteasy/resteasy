package org.jboss.resteasy.test.resource;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.FormParamResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests to make sure that FormParam are handled correctly
 */
public class FormParamInjectionTest {

    ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
    MockHttpResponse resp = new MockHttpResponse();

    @BeforeEach
    public void setup() {
        registry.addPerRequestResource(FormParamResource.class);
    }

    @Test
    public void testNoSplitAtSuccessiveEqualSign() throws Exception {
        MockHttpRequest req = createMockHttpRequest("/form/split", "valueA=v1%3Dv2=v3");
        Assertions.assertEquals("v1=v2=v3", registry.getResourceInvoker(req).invoke(req, resp)
                .getEntity());
    }

    /**
     * Builds an instance of {@code MockHttpRequest} properly configured to make sure that all
     * the {@code FormParam} are properly injected in all test cases otherwise we end up with
     * a {@code NullPointerException}.
     *
     * @param uri  the uri of the endpoint to test.
     * @param form the encoded form that shall be sent in the form body.
     * @return an instance of {@code MockHttpRequest} properly configured.
     * @throws Exception in case the provided uri is not properly formed.
     */
    private static MockHttpRequest createMockHttpRequest(String uri, String form) throws Exception {
        MockHttpRequest req = MockHttpRequest.post(uri);
        req.contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE.withCharset("UTF-8"));
        req.content(form.getBytes("UTF-8"));
        return req;
    }
}
