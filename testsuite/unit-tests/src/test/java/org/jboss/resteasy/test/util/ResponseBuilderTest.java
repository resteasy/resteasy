package org.jboss.resteasy.test.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.test.util.resource.ResponseBuilderRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.core.Response#ResponseBuilder class.
 * @tpSince RESTEasy 3.0.16
 */
public class ResponseBuilderTest {
    private static final URI BASE_URI = URI.create("http://localhost/");
    private static final URI REQUEST_URI = URI.create("http://localhost/path/to/resource");
    private static final String ERROR_MSG = "ResponseBuilder works incorrectly";

    private Response.ResponseBuilder builder;

    @BeforeEach
    public void before() throws URISyntaxException {
        HttpRequest httpRequest = MockHttpRequest.create("GET", REQUEST_URI,
                BASE_URI);

        ResteasyContext.getContextDataMap().put(HttpRequest.class,
                httpRequest);

        builder = new ResponseBuilderImpl();
    }

    @AfterEach
    public void after() throws Exception {
        ResteasyContext.removeContextDataLevel();
    }

    /**
     * @tpTestDetails Complex test for all relevant functions of ResponseBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void responseBuilderTest() {
        // testLocationSimple
        {
            Response r = builder.location(URI.create("/res")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assertions.assertEquals("http://localhost/res", actualUri, ERROR_MSG);
        }

        // testAllowed
        {
            Response response = Response.status(Response.Status.OK).allow("GET", "POST", "DELETE").build();
            Set<String> allowedMethods = response.getAllowedMethods();
            Assertions.assertEquals(allowedMethods.size(), 3, ERROR_MSG);
            Assertions.assertTrue(allowedMethods.contains("GET"), ERROR_MSG);
            Assertions.assertTrue(allowedMethods.contains("POST"), ERROR_MSG);
            Assertions.assertTrue(allowedMethods.contains("DELETE"), ERROR_MSG);
        }

        // testLocationPath
        {
            Response r = builder.location(URI.create("/a/res")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assertions.assertEquals("http://localhost/a/res", actualUri, ERROR_MSG);
        }

        // testLocationQueryString
        {
            Response r = builder.location(URI.create("/res?query")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assertions.assertEquals("http://localhost/res?query", actualUri, ERROR_MSG);
        }

        // testLocationFragment
        {
            Response r = builder.location(URI.create("/res#frag")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assertions.assertEquals("http://localhost/res#frag", actualUri, ERROR_MSG);
        }

        // testContentLocationSimple
        {
            Response r = builder.contentLocation(URI.create("/res")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assertions.assertEquals("http://localhost/res", actualUri, ERROR_MSG);
        }

        // testContentLocationPath
        {
            Response r = builder.contentLocation(URI.create("/a/res")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assertions.assertEquals("http://localhost/a/res", actualUri, ERROR_MSG);
        }

        // testContentLocationQueryString
        {
            Response r = builder.location(URI.create("/res?query")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assertions.assertEquals("http://localhost/res?query", actualUri, ERROR_MSG);
        }

        // testContentLocationFragment
        {
            Response r = builder.contentLocation(URI.create("/res#frag")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assertions.assertEquals("http://localhost/res#frag", actualUri, ERROR_MSG);
        }

        // testReplace
        {
            String[] headers = { "header1", "header2", "header3" };
            Response response = Response.ok().header(headers[0], headers[0])
                    .header(headers[1], headers[1]).header(headers[2], headers[2])
                    .replaceAll(null).build();
            for (String header : headers) {
                Assertions.assertTrue(response.getHeaderString(header) == null, ERROR_MSG);
            }
        }

        // allowStringArrayTruncateDuplicatesTest
        {
            String[] methods = { ResponseBuilderRequest.OPTIONS.name(), ResponseBuilderRequest.OPTIONS.name() };
            Response.ResponseBuilder rb = RuntimeDelegate.getInstance()
                    .createResponseBuilder();
            Response response = rb.allow(methods).build();
            Set<String> set = response.getAllowedMethods();
            Assertions.assertEquals(1, set.size(), ERROR_MSG);
            Assertions.assertEquals(set.iterator().next(), ResponseBuilderRequest.OPTIONS.name(), ERROR_MSG);
        }

        // testLinkHeaders
        {
            Response r1 = Response.ok()
                    .link("Link1", "rel1")
                    .links(
                            Link.fromUri("Link2").rel("rel2").build(),
                            Link.fromUri("Link3").rel("rel3").build())
                    .link("Link4", "rel4").build();

            Assertions.assertEquals(4, r1.getLinks().size(), ERROR_MSG);
            Assertions.assertNotNull(r1.getLink("rel1"), "Link-Header 'Link1' missing");
            Assertions.assertNotNull(r1.getLink("rel2"), "Link-Header 'Link2' missing");
            Assertions.assertNotNull(r1.getLink("rel3"), "Link-Header 'Link3' missing");
            Assertions.assertNotNull(r1.getLink("rel4"), "Link-Header 'Link4' missing");

            @SuppressWarnings(value = "unchecked")
            Response r2 = Response.ok()
                    .link("Link1", "rel1")
                    .links((Link[]) null)
                    .link("Link2", "rel2").build();

            Assertions.assertEquals(1, r2.getLinks().size(), ERROR_MSG);
            Assertions.assertNull(r2.getLink("rel1"), "Link-Header 'Link1' was not removed");
            Assertions.assertNotNull(r2.getLink("rel2"), "Link-Header 'Link2' missing");
        }
    }
}
