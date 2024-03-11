package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.HttponlyCookieResource;
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
 * @tpSubChapter NewCookie httponly flag is processed
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.0.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HttponlyCookieTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HttponlyCookieTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, HttponlyCookieResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HttponlyCookieTest.class.getSimpleName());
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

    @Test
    public void testHttponlyTrue() {
        WebTarget target = client.target(generateURL("/cookie/true"));
        Response response = target.request().get();
        NewCookie cookie = response.getCookies().entrySet().iterator().next().getValue();
        Assertions.assertNotNull(cookie);
        Assertions.assertTrue(cookie.isHttpOnly());
    }

    @Test
    public void testHttponlyDefault() {
        WebTarget target = client.target(generateURL("/cookie/default"));
        Response response = target.request().get();
        NewCookie cookie = response.getCookies().entrySet().iterator().next().getValue();
        Assertions.assertNotNull(cookie);
        Assertions.assertFalse(cookie.isHttpOnly());
    }
}
