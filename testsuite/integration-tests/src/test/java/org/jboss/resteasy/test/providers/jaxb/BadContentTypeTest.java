package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.BadContentTypeTestBean;
import org.jboss.resteasy.test.providers.jaxb.resource.BadContenTypeTestResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BadContentTypeTest {

    private static Logger logger = Logger.getLogger(BadContentTypeTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(BadContentTypeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, BadContenTypeTestResource.class, BadContentTypeTestBean.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, BadContentTypeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests if correct Response code is returned when sending syntactically incorrect xml
     * @tpInfo RESTEASY-519
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadRequest() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().post(Entity.entity("<junk", "application/xml"));
        Assert.assertEquals("The returned response status is not the expected one",
                Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * @tpTestDetails Tests if correct exception and MessageBodyWriter error is thrown when sending request for which no
     * MessageBodyWriterExists
     * @tpInfo RESTEASY-169
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHtmlError() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().header("Accept", "text/html").get();
        String stringResp = response.readEntity(String.class);
        logger.info("response: " + stringResp);
        assertEquals("The returned response status is not the expected one",
                HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue("The unexpected error response was thrown", stringResp.contains("media type: text/html"));
    }

    /**
     * @tpTestDetails Tests if correct HTTP 406 status code is returned when the specified accept media type
     * is not supported by the server
     */
    @Test
    public void testNotAcceptable() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test/foo"));
        Response response = target.request().header("Accept", "text/plain").get();
        assertEquals("The returned response status is not the expected one",
                HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
    }

    /**
     * @tpTestDetails Tests of receiving Bad Request response code after html error
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadRequestAfterHtmlError() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().post(Entity.entity("<junk", "application/xml"));
        Assert.assertEquals("The returned response status is not the expected one",
                Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response.close();

        response = target.request().header("Accept", "text/html").get();
        String stringResp = response.readEntity(String.class);
        logger.info("response: " + stringResp);
        assertEquals("The returned response status is not the expected one",
                HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue("The unexpected error response was thrown", stringResp.contains("media type: text/html"));

    }

}
