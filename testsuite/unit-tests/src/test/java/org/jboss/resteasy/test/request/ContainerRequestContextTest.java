package org.jboss.resteasy.test.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.core.interception.PreMatchContainerRequestContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Request
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4707
 */
public class ContainerRequestContextTest {

    private HttpRequest request;
    protected static final Logger logger = LogManager.getLogger(ContainerRequestContextTest.class.getName());

    @Before
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

        assertEquals("Wrong count of parameters in getUriInfo response", 2, containerRequestContext.getUriInfo().getQueryParameters().size());

        MultivaluedMap<String, String> expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));
        expected.put("bar", Collections.singletonList("bar"));

        assertEquals("Wrong parameter in getUriInfo response", expected, containerRequestContext.getUriInfo().getQueryParameters());

        containerRequestContext.setRequestUri(new URI("http://foo.bar"));
        logger.info("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        assertTrue("Wrong count of parameters in getUriInfo response", containerRequestContext.getUriInfo().getQueryParameters().isEmpty());

        containerRequestContext.setRequestUri(new URI("http://foo.bar?foo=foo"));
        logger.info("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));

        assertEquals("Wrong parameter in getUriInfo response", expected, containerRequestContext.getUriInfo().getQueryParameters());
    }
}
