package org.jboss.resteasy.test.response;

import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.response.resource.CookieResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CookieTest {

    protected final Logger logger = Logger.getLogger(VariantsTest.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CookieTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CookieResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CookieTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Client sends GET request to the server, server text response, the response is then checked, that
     *                it contains cookie with guid parameter sent by server.
     * @tpPassCrit Response contains cookie sent by server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWeirdCookie() {
        Response response = client.target(generateURL("/cookie/weird")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
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
        Assertions.assertTrue(cookies.containsKey("guid"),
                "Cookie in the response doesn't contain 'guid', which was in the cookie send by the server.");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request to the server, server text response, the response is then checked, that
     *                it contains standard cookie sent by server.
     * @tpPassCrit Response contains cookie sent by server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStandardCookie() {
        Response response = client.target(generateURL("/cookie/standard")).request().get();
        Assertions.assertEquals(response.getStatus(), 200);
        for (Map.Entry<String, NewCookie> cookieEntry : response.getCookies().entrySet()) {
            //            logger.debug("[" + cookieEntry.getKey() + "] >>>>>> " + cookieEntry.getValue() + "");
            Assertions.assertEquals("UserID=JohnDoe;Version=1;Max-Age=3600", cookieEntry.getValue().toString(),
                    "Cookie content in the response doesn't match the cookie content send by the server.");
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
