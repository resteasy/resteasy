package org.jboss.resteasy.test.resource.request;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.request.resource.PreconditionEtagResource;
import org.jboss.resteasy.test.resource.request.resource.PreconditionLastModifiedResource;
import org.jboss.resteasy.test.resource.request.resource.PreconditionPrecedenceResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for preconditions specified in the header of the request
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PreconditionTest {

    static Client client;
    static WebTarget precedenceWebTarget;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
        precedenceWebTarget = client.target(generateURL("/precedence"));
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PreconditionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PreconditionLastModifiedResource.class, PreconditionEtagResource.class,
                PreconditionPrecedenceResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PreconditionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Sets header IF_UNMODIFIED_SINCE date before last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceBeforeLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets header IF_UNMODIFIED_SINCE date after last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceAfterLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets header IF_MODIFIED_SINCE date before last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfModifiedSinceBeforeLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets header IF_MODIFIED_SINCE date after last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfModifiedSinceAfterLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets headers IF_MODIFIED_SINCE and IF_UNMODIFIED_SINCE date before last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceBeforeLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT")
                    .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets headers IF_MODIFIED_SINCE date after last modified and IF_UNMODIFIED_SINCE date before last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceAfterLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT")
                    .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets headers IF_MODIFIED_SINCE and IF_UNMODIFIED_SINCE date after last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceAfterLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT")
                    .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets headers IF_MODIFIED_SINCE date before last modified and IF_UNMODIFIED_SINCE date after last modified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceBeforeLastModified() {
        WebTarget base = client.target(generateURL("/"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT")
                    .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets header IF_MATCH to an entity value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithMatchingETag() {
        testIfMatchWithMatchingETag("");
        testIfMatchWithMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_MATCH to an entity value which doesn't match to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithoutMatchingETag() {
        testIfMatchWithoutMatchingETag("");
        testIfMatchWithoutMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_MATCH to a wildcard
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWildCard() {
        testIfMatchWildCard("");
        testIfMatchWildCard("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to an entity value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfNonMatchWithMatchingETag() {
        testIfNonMatchWithMatchingETag("");
        testIfNonMatchWithMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to an entity value which doesn't match to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfNonMatchWithoutMatchingETag() {
        testIfNonMatchWithoutMatchingETag("");
        testIfNonMatchWithoutMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to a wildcard
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfNonMatchWildCard() {
        testIfNonMatchWildCard("");
        testIfNonMatchWildCard("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH and IF_MATCH to an entity value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag() {
        testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("");
        testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("/fromField");

    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to an entity value which doesn't match to eTag in the resource
     * and IF_MATCH to an entity value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag() {
        testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("");
        testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to an entity value which matches to eTag in the resource
     * and IF_MATCH to an entity value which doesn't match to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag() {
        testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("");
        testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_NONE_MATCH to an entity value which doesn't match to eTag in the resource
     * and IF_MATCH to an entity value which doesn't match to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag() {
        testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("");
        testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
    }

    /**
     * @tpTestDetails Sets header IF_MATCH to an weak eTag value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithMatchingWeakETag() {
        WebTarget base = client.target(generateURL("/etag/weak"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "W/\"1\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Sets header IF_MATCH to an weak eTag value which matches to eTag in the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIfMatchWithNonMatchingWeakEtag() {
        WebTarget base = client.target(generateURL("/etag/weak"));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "W/\"2\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ////////////

    public void testIfMatchWithMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"1\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWithoutMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"2\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWildCard(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "*").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfNonMatchWithMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_NONE_MATCH, "\"1\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            Assert.assertEquals("The eTag in the response doesn't match",
                    "\"1\"", response.getStringHeaders().getFirst(HttpHeaderNames.ETAG));
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfNonMatchWithoutMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_NONE_MATCH, "2").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfNonMatchWildCard(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_NONE_MATCH, "*").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            Assert.assertEquals("The eTag in the response doesn't match",
                    "\"1\"", response.getStringHeaders().getFirst(HttpHeaderNames.ETAG));
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"1\"")
                    .header(HttpHeaderNames.IF_NONE_MATCH, "\"1\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
            Assert.assertEquals("The eTag in the response doesn't match",
                    "\"1\"", response.getStringHeaders().getFirst(HttpHeaderNames.ETAG));
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"1\"")
                    .header(HttpHeaderNames.IF_NONE_MATCH, "\"2\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"2\"")
                    .header(HttpHeaderNames.IF_NONE_MATCH, "\"1\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag(String fromField) {
        WebTarget base = client.target(generateURL("/etag" + fromField));
        try {
            Response response = base.request().header(HttpHeaderNames.IF_MATCH, "\"2\"")
                    .header(HttpHeaderNames.IF_NONE_MATCH, "\"2\"").get();
            Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @tpTestDetails Response if all match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_AllMatch() {
        Response response = precedenceWebTarget.request()
                .header(HttpHeaderNames.IF_MATCH, "1")  // true
                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if all match without IF_MATCH
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfMatchWithNonMatchingEtag() {
        Response response = precedenceWebTarget.request()
                .header(HttpHeaderNames.IF_MATCH, "2")  // false
                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if all match without IF_MATCH
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfMatchNotPresentUnmodifiedSinceBeforeLastModified() {
        Response response = precedenceWebTarget.request()

                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") //false
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MATH is missing
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchWithMatchingEtag() {
        Response response = precedenceWebTarget.request()
                .header(HttpHeaderNames.IF_NONE_MATCH, "1")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MATH is missing and IF_NONE_MATCH don't match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchWithNonMatchingEtag() {
        Response response = precedenceWebTarget.request()
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // false
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MODIFIED_SINCE don't match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceBeforeLastModified() {
        Response response = precedenceWebTarget.request()

                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") // false
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MODIFIED_SINCE match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceAfterLastModified() {
        Response response = precedenceWebTarget.request()
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT")  // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
        response.close();
    }
}
