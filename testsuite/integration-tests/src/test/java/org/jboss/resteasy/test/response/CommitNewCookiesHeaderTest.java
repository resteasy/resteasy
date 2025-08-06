package org.jboss.resteasy.test.response;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CommitNewCookiesHeaderTest {

    @Path("echo")
    public static class EchoResource {

        @Produces(MediaType.TEXT_PLAIN)
        @GET
        public Response echo(@QueryParam("msg") String msg) {
            // send cookie as a simple string
            NewCookie nck2 = new NewCookie.Builder("Cookie 2")
                    .value("Cookie 2 value")
                    .build();
            NewCookie nck3 = new NewCookie.Builder("Cookie 3")
                    .value("Cookie 3 value")
                    .build();
            return Response.ok(msg).header(HttpHeaders.SET_COOKIE, "Cookie 1=Cookie 1 value;Version=1;Path=/")
                    .cookie(nck2, nck3)
                    .build();
        }

        @Path("two")
        @Produces(MediaType.TEXT_PLAIN)
        @GET
        public Response echoTwo(@QueryParam("msg") String msg) {
            // Any class that provides a toString can be provided as a cookie
            NewCookie nck1 = new NewCookie.Builder("Cookie 2")
                    .value("Cookie 2 value")
                    .build();
            return Response.ok().header(HttpHeaders.SET_COOKIE,
                    new Object() {
                        @Override
                        public String toString() {
                            return "Cookie 1=Cookie 1 value;Version=1;Path=/";
                        }
                    })
                    .cookie(nck1)
                    .build();
        }

        @Path("three")
        @Produces(MediaType.TEXT_PLAIN)
        @GET
        public Response echoThree(@QueryParam("msg") String msg) {
            // Cookie should really only be used with request but it is an object with a toString impl
            Cookie ck1 = new Cookie.Builder("Cookie 1")
                    .value("Cookie 1 value")
                    .build();
            NewCookie nck1 = new NewCookie.Builder("Cookie 2")
                    .value("Cookie 2 value")
                    .build();
            return Response.ok(msg).header(HttpHeaders.SET_COOKIE, ck1)
                    .cookie(nck1)
                    .build();
        }

    }

    private static Client client;
    private static final String DEP = "CommitCookiesHeaderTest";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DEP);
        war.addClass(EchoResource.class);
        return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(DEP);
    }

    @Test
    public void testAcceptApplicationStar() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
                .request(MediaType.TEXT_PLAIN_TYPE);
        try (Response response = request.get()) {
            Map<String, NewCookie> cookies = response.getCookies();
            Assertions.assertEquals(3, cookies.size());
            Assertions.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
            Assertions.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
            Assertions.assertEquals("Cookie 3 value", cookies.get("Cookie 3").getValue());
        }
    }

    @Test
    public void testSecondCase() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo/two").queryParam("msg", "Hello world")
                .request(MediaType.TEXT_PLAIN_TYPE);
        try (Response response = request.get()) {
            Map<String, NewCookie> cookies = response.getCookies();
            Assertions.assertEquals(2, cookies.size());
            Assertions.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
            Assertions.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
        }
    }

    @Test
    public void testThreeCase() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo/three").queryParam("msg", "Hello world")
                .request(MediaType.TEXT_PLAIN_TYPE);
        try (Response response = request.get()) {
            Map<String, NewCookie> cookies = response.getCookies();
            Assertions.assertEquals(2, cookies.size());
            Assertions.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
            Assertions.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
        }
    }
}
