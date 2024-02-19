package org.jboss.resteasy.test.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Request
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4707
 */
public class ContainerRequestContextTest {

    private HttpRequest request;
    protected static final Logger logger = Logger.getLogger(ContainerRequestContextTest.class.getName());

    @BeforeEach
    public void before() throws URISyntaxException {
        request = MockHttpRequest.create("GET", "http://foo.bar?foo=foo&bar=bar");
    }

    /**
     * @tpTestDetails Test that ContainerRequestContext setRequestUri clear previous query parameters
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testQueryParamatersClear() throws URISyntaxException {
        ContainerRequestContext containerRequestContext = new PreMatchContainerRequestContext(request, null, null);

        logger.info("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        assertEquals(2, containerRequestContext.getUriInfo().getQueryParameters().size(),
                "Wrong count of parameters in getUriInfo response");

        MultivaluedMap<String, String> expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));
        expected.put("bar", Collections.singletonList("bar"));

        MultivaluedMap<String, String> queryParameters = containerRequestContext.getUriInfo().getQueryParameters();
        assertEquals(expected, queryParameters, "Wrong parameter in getUriInfo response");

        containerRequestContext.setRequestUri(new URI("http://foo.bar"));
        logger.info("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        assertTrue(containerRequestContext.getUriInfo().getQueryParameters().isEmpty(),
                "Wrong count of parameters in getUriInfo response");

        containerRequestContext.setRequestUri(new URI("http://foo.bar?foo=foo"));
        logger.info("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));

        assertEquals(expected, containerRequestContext.getUriInfo().getQueryParameters(),
                "Wrong parameter in getUriInfo response");
    }
}
