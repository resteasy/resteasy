package org.jboss.resteasy.test.util;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.util.resource.ResponseBuilderRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for javax.ws.rs.core.Response#ResponseBuilder class.
 * @tpSince RESTEasy 3.0.16
 */
public class ResponseBuilderTest {
    private static final URI BASE_URI = URI.create("http://localhost/");
    private static final URI REQUEST_URI = URI.create("http://localhost/path/to/resource");
    private static final String ERROR_MSG = "ResponseBuilder works incorrectly";

    private Response.ResponseBuilder builder;

    @Before
    public void before() throws URISyntaxException {
        HttpRequest httpRequest = MockHttpRequest.create("GET", REQUEST_URI,
                BASE_URI);

        ResteasyProviderFactory.getContextDataMap().put(HttpRequest.class,
                httpRequest);

        builder = new ResponseBuilderImpl();
    }

    @After
    public void after() throws Exception {
        ResteasyProviderFactory.removeContextDataLevel();
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

            Assert.assertEquals(ERROR_MSG, "http://localhost/res", actualUri);
        }

        // testAllowed
        {
            Response response = Response.status(Response.Status.OK).allow("GET", "POST", "DELETE").build();
            Set<String> allowedMethods = response.getAllowedMethods();
            Assert.assertEquals(ERROR_MSG, allowedMethods.size(), 3);
            Assert.assertTrue(ERROR_MSG, allowedMethods.contains("GET"));
            Assert.assertTrue(ERROR_MSG, allowedMethods.contains("POST"));
            Assert.assertTrue(ERROR_MSG, allowedMethods.contains("DELETE"));
        }

        // testLocationPath
        {
            Response r = builder.location(URI.create("/a/res")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/a/res", actualUri);
        }

        // testLocationQueryString
        {
            Response r = builder.location(URI.create("/res?query")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/res?query", actualUri);
        }

        // testLocationFragment
        {
            Response r = builder.location(URI.create("/res#frag")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/res#frag", actualUri);
        }

        // testContentLocationSimple
        {
            Response r = builder.contentLocation(URI.create("/res")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/res", actualUri);
        }

        // testContentLocationPath
        {
            Response r = builder.contentLocation(URI.create("/a/res")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/a/res", actualUri);
        }

        // testContentLocationQueryString
        {
            Response r = builder.location(URI.create("/res?query")).build();
            String actualUri = r.getMetadata().getFirst("Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/res?query", actualUri);
        }

        // testContentLocationFragment
        {
            Response r = builder.contentLocation(URI.create("/res#frag")).build();
            String actualUri = r.getMetadata().getFirst("Content-Location").toString();

            Assert.assertEquals(ERROR_MSG, "http://localhost/res#frag", actualUri);
        }

        // testReplace
        {
            String[] headers = {"header1", "header2", "header3"};
            Response response = Response.ok().header(headers[0], headers[0])
                    .header(headers[1], headers[1]).header(headers[2], headers[2])
                    .replaceAll(null).build();
            for (String header : headers) {
                Assert.assertTrue(ERROR_MSG, response.getHeaderString(header) == null);
            }
        }

        // allowStringArrayTruncateDuplicatesTest
        {
            String[] methods = {ResponseBuilderRequest.OPTIONS.name(), ResponseBuilderRequest.OPTIONS.name()};
            Response.ResponseBuilder rb = RuntimeDelegate.getInstance()
                    .createResponseBuilder();
            Response response = rb.allow(methods).build();
            Set<String> set = response.getAllowedMethods();
            Assert.assertEquals(ERROR_MSG, 1, set.size());
            Assert.assertEquals(ERROR_MSG, set.iterator().next(), ResponseBuilderRequest.OPTIONS.name());
        }

        // testLinkHeaders
        {
            Response r1 = Response.ok()
                    .link("Link1", "rel1")
                    .links(
                            Link.fromUri("Link2").rel("rel2").build(),
                            Link.fromUri("Link3").rel("rel3").build()
                    )
                    .link("Link4", "rel4").build();

            Assert.assertEquals(ERROR_MSG, 4, r1.getLinks().size());
            Assert.assertNotNull("Link-Header 'Link1' missing", r1.getLink("rel1"));
            Assert.assertNotNull("Link-Header 'Link2' missing", r1.getLink("rel2"));
            Assert.assertNotNull("Link-Header 'Link3' missing", r1.getLink("rel3"));
            Assert.assertNotNull("Link-Header 'Link4' missing", r1.getLink("rel4"));

            @SuppressWarnings(value = "unchecked")
            Response r2 = Response.ok()
                    .link("Link1", "rel1")
                    .links((Link[]) null)
                    .link("Link2", "rel2").build();

            Assert.assertEquals(ERROR_MSG, 1, r2.getLinks().size());
            Assert.assertNull("Link-Header 'Link1' was not removed", r2.getLink("rel1"));
            Assert.assertNotNull("Link-Header 'Link2' missing", r2.getLink("rel2"));
        }
    }
}
