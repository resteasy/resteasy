package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.interception.PreMatchContainerRequestContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContainerRequestContextTest {

    private HttpRequest request;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
    }

    @Before
    public void before() throws URISyntaxException {
        request = MockHttpRequest.create("GET", "http://foo.bar?foo=foo&bar=bar");
    }

    @Test
    public void testQueryParamatersClear() throws URISyntaxException {
        ContainerRequestContext containerRequestContext = new PreMatchContainerRequestContext(request);

        System.out.println("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        assertEquals(2, containerRequestContext.getUriInfo().getQueryParameters().size());

        MultivaluedMap<String, String> expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));
        expected.put("bar", Collections.singletonList("bar"));

        assertEquals(expected, containerRequestContext.getUriInfo().getQueryParameters());

        containerRequestContext.setRequestUri(new URI("http://foo.bar"));
        System.out.println("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        assertTrue(containerRequestContext.getUriInfo().getQueryParameters().isEmpty());

        containerRequestContext.setRequestUri(new URI("http://foo.bar?foo=foo"));
        System.out.println("request uri: " + containerRequestContext.getUriInfo().getRequestUri());

        expected = new MultivaluedHashMap<>();
        expected.put("foo", Collections.singletonList("foo"));

        assertEquals(expected, containerRequestContext.getUriInfo().getQueryParameters());
    }
}
