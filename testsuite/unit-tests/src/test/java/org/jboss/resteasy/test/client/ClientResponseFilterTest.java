package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterAbortWith;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterAllowed;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterHeaders;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterInterceptorReaderOne;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterInterceptorReaderTwo;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterLength;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterNullHeaderString;
import org.jboss.resteasy.test.client.resource.ClientResponseFilterStatusOverride;
import org.jboss.resteasy.test.client.resource.NullStringBeanRuntimeDelegate;
import org.jboss.resteasy.test.client.resource.StringBean;
import org.jboss.resteasy.test.client.resource.StringBeanRuntimeDelegate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
public class ClientResponseFilterTest {

    static Client client;

    String dummyUrl = "dummyUrl";

    @BeforeAll
    public static void setupClient() {
        client = ClientBuilder.newClient();

    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and ClientResponseFilter. The first returns Response provided
     *                as argument. The latter processes the response and checks that the header of the response is not null.
     *                Note: This test uses custom implementation of RuntimeDelegate, allowing to customize conversion between
     *                String
     *                representation of HTTP header and the corresponding JAX-RS type (StringBean in this case)
     * @tpPassCrit Successful response is returned and response header is not null
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void emptyHeaderStringTest() {

        RuntimeDelegate original = RuntimeDelegate.getInstance();
        RuntimeDelegate.setInstance(new NullStringBeanRuntimeDelegate(original));
        try {
            Response abortWith = Response.ok().header("header1", new StringBean("aa"))
                    .build();
            Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(abortWith))
                    .register(ClientResponseFilterNullHeaderString.class).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        } finally {
            RuntimeDelegate.setInstance(original);
            StringBeanRuntimeDelegate.assertNotStringBeanRuntimeDelegate();
        }

    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and ClientResponseFilter. The first returns Response provided
     *                as argument. The latter processes the response and checks that the length of the response is same
     *                as in the original response
     * @tpPassCrit Successful response is returned and response length is same as original response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void lengthTest() {
        Response abortWith = Response.ok()
                .header(HttpHeaders.CONTENT_LENGTH, 10).build();
        Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(abortWith))
                .register(ClientResponseFilterLength.class).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and ClientResponseFilter. The first returns Response provided
     *                as argument. The latter processes the response and checks that response contains header 'OPTIONS' as
     *                allowed method
     * @tpPassCrit Successful response is returned and 'OPTIONS' method is allowed header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void allowedTest() {
        Response abortWith = Response.ok().header(HttpHeaders.ALLOW, "get")
                .header(HttpHeaders.ALLOW, "options").build();
        Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(abortWith))
                .register(ClientResponseFilterAllowed.class).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and ClientResponseFilter. The first returns Response provided
     *                as argument. The latter processes the response and changes the response code to 'FORBIDDEN'
     * @tpPassCrit Response with status code 'FORBIDDEN' is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void statusOverrideTest() {
        Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(Response.ok().build()))
                .register(ClientResponseFilterStatusOverride.class).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and ClientResponseFilter. The first returns Response provided
     *                as argument. The latter processes the response and prints all headers in the response
     * @tpPassCrit The response with reponse code success is expected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void headersTest() {
        Response.ResponseBuilder builder = Response.ok()
                .header("header", MediaType.APPLICATION_ATOM_XML_TYPE)
                .entity("entity");
        Response abortWith = builder.build();
        Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(abortWith))
                .register(ClientResponseFilterHeaders.class).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }

    /**
     * @tpTestDetails Client registers ClientRequestFilter and two ReaderInterceptors. The first returns Response provided
     *                as argument. The ReaderInterceptorOne calls ReaderInterceptorTwo and catches IOException raised by
     *                ReaderInterceptorTwo
     * @tpPassCrit The ReaderInterceptorOne catches IOException and sends successful response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void interceptorOrderTest() {
        Response.ResponseBuilder builder = Response.ok()
                .header("header", MediaType.APPLICATION_ATOM_XML_TYPE)
                .entity("entity");
        Response abortWith = builder.build();
        Response response = client.target(dummyUrl).register(new ClientResponseFilterAbortWith(abortWith))
                .register(ClientResponseFilterInterceptorReaderTwo.class)
                .register(ClientResponseFilterInterceptorReaderOne.class)
                .request().get();
        String str = response.readEntity(String.class);
        Assertions.assertEquals("OK", str,
                "First ReaderInterceptor one didn't catch exception raised by ReaderInterceptor two");
    }
}
