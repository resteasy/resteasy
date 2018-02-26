package org.jboss.resteasy.test.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.CookieResource;
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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CookieTest {

    protected final Logger logger = LogManager.getLogger(VariantsTest.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CookieTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CookieResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CookieTest.class.getSimpleName());
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }


    /**
     * @tpTestDetails Client sends GET request to the server, server text response, the response is then checked, that
     * it contains cookie with guid parameter sent by server.
     * @tpPassCrit Response contains cookie sent by server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWeirdCookie() {
        Response response = client.target(generateURL("/cookie/weird")).request().get();
        Assert.assertEquals(response.getStatus(), 200);
        Map<String, NewCookie> cookies = response.getCookies();
//        for (Map.Entry<String, NewCookie> cookieEntry : cookies.entrySet()) {
//            logger.debug("[" + cookieEntry.getKey() + "] >>>>>> " + cookieEntry.getValue() + "");
//        }
//        for (Map.Entry<String, List<String>> headerEntry : response.getStringHeaders().entrySet()) {
//            logger.debug("header: " + headerEntry.getKey());
//            for (String val : headerEntry.getValue()) {
//                logger.debug("    " + val);
//            }
//        }
        Assert.assertTrue("Cookie in the response doesn't contain 'guid', which was in the cookie send by the server.",
                cookies.containsKey("guid"));
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request to the server, server text response, the response is then checked, that
     * it contains standard cookie sent by server.
     * @tpPassCrit Response contains cookie sent by server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStandardCookie() {
        Response response = client.target(generateURL("/cookie/standard")).request().get();
        Assert.assertEquals(response.getStatus(), 200);
        for (Map.Entry<String, NewCookie> cookieEntry : response.getCookies().entrySet()) {
//            logger.debug("[" + cookieEntry.getKey() + "] >>>>>> " + cookieEntry.getValue() + "");
            Assert.assertEquals("Cookie content in the response doesn't match the cookie content send by the server.",
                    "UserID=JohnDoe;Version=1;Max-Age=3600", cookieEntry.getValue().toString());
        }
//        for (Map.Entry<String, List<String>> headerEntry : response.getStringHeaders().entrySet()) {
//            logger.debug("header: " + headerEntry.getKey());
//            for (String val : headerEntry.getValue()) {
//                logger.debug("    " + val);
//            }
//        }
        response.close();
    }

}
