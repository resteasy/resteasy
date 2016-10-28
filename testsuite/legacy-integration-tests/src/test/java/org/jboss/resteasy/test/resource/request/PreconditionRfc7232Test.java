package org.jboss.resteasy.test.resource.request;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.resource.request.resource.PreconditionRfc7232PrecedenceResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for RFC 7232 functionality
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PreconditionRfc7232Test {
    private Client client;
    private WebTarget webTarget;

    @Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
        webTarget = client.target(generateURL("/precedence"));
    }

    @After
    public void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PreconditionRfc7232Test.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        Map<String, String> initParams = new HashMap<>();
        initParams.put("resteasy.rfc7232preconditions", "true");
        return TestUtil.finishContainerPrepare(war, initParams, PreconditionRfc7232PrecedenceResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PreconditionRfc7232Test.class.getSimpleName());
    }

    /**
     * @tpTestDetails Response if all match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_AllMatch() {
        Response response = webTarget.request().header(HttpHeaderNames.IF_MATCH, "1")  // true
                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();  // true

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if all match without IF_MATCH
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfMatchWithNonMatchingEtag() {
        Response response = webTarget.request()
                .header(HttpHeaderNames.IF_MATCH, "2")  // false
                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();  // true

        Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if all match without IF_MATCH
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfMatchNotPresentUnmodifiedSinceBeforeLastModified() {
        Response response = webTarget.request()
                .header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT") //false
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // true
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT").get();  // true

        Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MATH is missing
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchWithMatchingEtag() {
        Response response = webTarget.request()
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
    @Category({NotForForwardCompatibility.class})
    public void testPrecedence_IfNoneMatchWithNonMatchingEtag() {
        Response response = webTarget.request()
                .header(HttpHeaderNames.IF_NONE_MATCH, "2")  // false
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT")  // true
                .get();
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-4705"), HttpResponseCodes.SC_OK, response.getStatus());

        response.close();
    }

    /**
     * @tpTestDetails Response if IF_MODIFIED_SINCE don't match
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceBeforeLastModified() {
        Response response = webTarget.request()
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
        Response response = webTarget.request()
                .header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT")  // true
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
        response.close();
    }
}
