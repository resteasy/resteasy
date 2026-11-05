package org.jboss.resteasy.test.providers.jaxb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.BadContenTypeTestResource;
import org.jboss.resteasy.test.providers.jaxb.resource.BadContentTypeTestBean;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BadContentTypeTest {

    private static Logger logger = Logger.getLogger(BadContentTypeTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(BadContentTypeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, BadContenTypeTestResource.class, BadContentTypeTestBean.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        // As of Jakarta REST 3.1 a default exception mapper is required and the default response code is 500.
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "The returned response status is not the expected one");
    }

    /**
     * @tpTestDetails Tests if correct exception and MessageBodyWriter error is thrown when sending request for which no
     *                MessageBodyWriterExists
     * @tpInfo RESTEASY-169
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHtmlError() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().header("Accept", "text/html").get();
        String stringResp = response.readEntity(String.class);
        logger.info("response: " + stringResp);
        // TODO (jrp) it needs to be determined if this is correct
        assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus(),
                () -> String.format("Invalid response code returned: %s", stringResp));
        //assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus(),
        //        "The returned response status is not the expected one");
        //assertTrue(stringResp.contains("media type: text/html"), "The unexpected error response was thrown");
    }

    /**
     * @tpTestDetails Tests if correct HTTP 406 status code is returned when the specified accept media type
     *                is not supported by the server
     */
    @Test
    public void testNotAcceptable() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test/foo"));
        Response response = target.request().header("Accept", "text/plain").get();
        assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus(),
                "The returned response status is not the expected one");
    }

    /**
     * @tpTestDetails Tests of receiving Bad Request response code after html error
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadRequestAfterHtmlError() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        Response response = target.request().post(Entity.entity("<junk", "application/xml"));
        // As of Jakarta REST 3.1 a default exception mapper is required and the default response code is 500.
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "The returned response status is not the expected one");
        response.close();

        response = target.request().header("Accept", "text/html").get();
        String stringResp = response.readEntity(String.class);
        logger.info("response: " + stringResp);
        assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus(),
                () -> String.format("Invalid response code returned: %s", stringResp));
        //assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus(),
        //        "The returned response status is not the expected one");
        //assertTrue(stringResp.contains("media type: text/html"), "The unexpected error response was thrown");

    }

}
