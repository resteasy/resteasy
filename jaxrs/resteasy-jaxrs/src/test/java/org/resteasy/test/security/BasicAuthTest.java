package org.resteasy.test.security;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.server.embedded.SimpleSecurityDomain;
import org.resteasy.test.TJWSServletContainer;
import org.resteasy.util.HttpResponseCodes;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthTest
{
   private static Dispatcher dispatcher;


   @Path("/secured")
   public static class BaseResource
   {
      @GET
      public String get(@Context SecurityContext ctx)
      {
         System.out.println("********* IN SECURE CLIENT");
         if (!ctx.isUserInRole("admin"))
         {
            System.out.println("NOT IN ROLE!!!!");
            throw new WebApplicationException(401);
         }
         return "hello";
      }

      @GET
      @Path("/authorized")
      @RolesAllowed("admin")
      public String getAuthorized()
      {
         return "authorized";
      }

      @GET
      @Path("/deny")
      @DenyAll
      public String deny()
      {
         return "SHOULD NOT BE REACHED";
      }
   }


   @BeforeClass
   public static void before() throws Exception
   {
      SimpleSecurityDomain domain = new SimpleSecurityDomain();
      String[] roles = {"admin"};
      String[] basic = {"user"};
      domain.addUser("bill", "password", roles);
      domain.addUser("mo", "password", basic);
      dispatcher = TJWSServletContainer.start("", domain);
      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      TJWSServletContainer.stop();
   }

   @Test
   public void testSecurity() throws Exception
   {
      HttpClient client = new HttpClient();
      client.getParams().setAuthenticationPreemptive(true);

      client.getState().setCredentials(
              //new AuthScope(null, 8080, "Test"),
              new AuthScope(AuthScope.ANY),
              new UsernamePasswordCredentials("bill", "password")
      );
      {
         GetMethod method = new GetMethod("http://localhost:8081/secured");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/secured/authorized");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("authorized", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/secured/deny");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
         method.releaseConnection();
      }
   }

   @Test
   public void testSecurityFailure() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8081/secured");
         int status = client.executeMethod(method);
         Assert.assertEquals(401, status);
         method.releaseConnection();
      }

      client.getParams().setAuthenticationPreemptive(true);

      client.getState().setCredentials(
              //new AuthScope(null, 8080, "Test"),
              new AuthScope(AuthScope.ANY),
              new UsernamePasswordCredentials("mo", "password")
      );
      {
         GetMethod method = new GetMethod("http://localhost:8081/secured/authorized");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
         method.releaseConnection();
      }
   }
}
