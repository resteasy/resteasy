package org.jboss.resteasy.test.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Nicolas NESMON
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for cookie management support in Resteasy client.
 * @tpSince RESTEasy
 *
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientCookieTest extends ClientTestBase {

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class ClienCookieResource {

        @GET
        @Path("createCookie")
        public Response createCookie() {
            NewCookie cookie = new NewCookie.Builder("Cookie")
                    .value("CookieValue")
                    .build();
            return Response.ok().cookie(cookie).build();
        }

        @GET
        @Path("getCookiesCount")
        public Response getCookiesCount(@Context HttpHeaders httpHeaders) {
            return Response.ok(httpHeaders.getCookies().size()).build();
        }

    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientCookieTest.class.getSimpleName());
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, ClienCookieResource.class);
    }

    @Test
    public void client_Should_NotStoreCookie_When_NotConfigured() {
        Client client = ClientBuilder.newClient();
        try {

            try (Response response = client.target(generateURL("/createCookie")).request(MediaType.TEXT_PLAIN_TYPE).get()) {
                NewCookie cookie = response.getCookies().get("Cookie");
                Assertions.assertNotNull(cookie);
            }

            int cookiesCount = client.target(generateURL("/getCookiesCount")).request(MediaType.TEXT_PLAIN_TYPE)
                    .get(Integer.class);
            Assertions.assertEquals(0, cookiesCount);

        } finally {
            client.close();
        }
    }

}
