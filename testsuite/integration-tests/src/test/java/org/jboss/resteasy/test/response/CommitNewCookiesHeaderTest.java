package org.jboss.resteasy.test.response;

import java.lang.reflect.ReflectPermission;
import java.util.Map;
import java.util.PropertyPermission;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class CommitNewCookiesHeaderTest {

   @Path("echo")
   public static class EchoResource {

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      public Response echo(@QueryParam("msg") String msg) {
         // send cookie as a simple string
         return Response.ok(msg).header(HttpHeaders.SET_COOKIE, "Cookie 1=Cookie 1 value;Version=1;Path=/")
                 .cookie(new NewCookie("Cookie 2", "Cookie 2 value"),
                         new NewCookie("Cookie 3", "Cookie 3 value"))
                 .build();
      }

      @Path("two")
      @Produces(MediaType.TEXT_PLAIN)
      @GET
      public Response echoTwo(@QueryParam("msg") String msg) {
         // Any class that provides a toString can be provided as a cookie
         return Response.ok().header(HttpHeaders.SET_COOKIE,
                 new Object() {
                     @Override
                     public String toString() {
                        return "Cookie 1=Cookie 1 value;Version=1;Path=/";
                     }
                  })
          .cookie(new NewCookie("Cookie 2", "Cookie 2 value"))
          .build();
      }

      @Path("three")
      @Produces(MediaType.TEXT_PLAIN)
      @GET
      public Response echoThree(@QueryParam("msg") String msg) {
         // Cookie should really only be used with request but it is an object with a toString impl
         return Response.ok(msg).header(HttpHeaders.SET_COOKIE,  new Cookie("Cookie 1", "Cookie 1 value"))
                 .cookie(new NewCookie("Cookie 2", "Cookie 2 value"))
                 .build();
      }

   }

   private static Client client;
   private static final String DEP = "CommitCookiesHeaderTest";

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(DEP);
      war.addClass(EchoResource.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new ReflectPermission("suppressAccessChecks"),
              new PropertyPermission("ipv6", "read"),
              new PropertyPermission("node", "read")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
   }

   @BeforeClass
   public static void setup() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
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
         Assert.assertEquals(3, cookies.size());
         Assert.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
         Assert.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
         Assert.assertEquals("Cookie 3 value", cookies.get("Cookie 3").getValue());
      }
   }

   @Test
   public void testSecondCase() throws Exception {
      Invocation.Builder request = client.target(generateURL()).path("echo/two").queryParam("msg", "Hello world")
              .request(MediaType.TEXT_PLAIN_TYPE);
      try (Response response = request.get()) {
         Map<String, NewCookie> cookies = response.getCookies();
         Assert.assertEquals(2, cookies.size());
         Assert.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
         Assert.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
      }
   }

   @Test
   public void testThreeCase() throws Exception {
      Invocation.Builder request = client.target(generateURL()).path("echo/three").queryParam("msg", "Hello world")
              .request(MediaType.TEXT_PLAIN_TYPE);
      try (Response response = request.get()) {
         Map<String, NewCookie> cookies = response.getCookies();
         Assert.assertEquals(2, cookies.size());
         Assert.assertEquals("Cookie 1 value", cookies.get("Cookie 1").getValue());
         Assert.assertEquals("Cookie 2 value", cookies.get("Cookie 2").getValue());
      }
   }
}
