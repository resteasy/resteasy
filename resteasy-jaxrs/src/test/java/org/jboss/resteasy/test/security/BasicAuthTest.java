package org.jboss.resteasy.test.security;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.embedded.SimpleSecurityDomain;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
      String[] roles =
              {"admin"};
      String[] basic =
              {"user"};
      domain.addUser("bill", "password", roles);
      domain.addUser("mo", "password", basic);
      dispatcher = EmbeddedContainer.start("", domain).getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testSecurity() throws Exception
   {
      HttpClient client = new HttpClient();
      client.getParams().setAuthenticationPreemptive(true);

      client.getState().setCredentials(
              //new AuthScope(null, 8080, "Test"),
              new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials("bill", "password"));
      {
         GetMethod method = createGetMethod("/secured");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/secured/authorized");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("authorized", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/secured/deny");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
         method.releaseConnection();
      }
      client.getHttpConnectionManager().closeIdleConnections(0);

   }

   @Test
   public void testSecurityFailure() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = createGetMethod("/secured");
         int status = client.executeMethod(method);
         Assert.assertEquals(401, status);
         method.releaseConnection();
      }

      client.getParams().setAuthenticationPreemptive(true);

      client.getState().setCredentials(
              //new AuthScope(null, 8080, "Test"),
              new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials("mo", "password"));
      {
         GetMethod method = createGetMethod("/secured/authorized");
         method.setDoAuthentication(true);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
         method.releaseConnection();
      }
      client.getHttpConnectionManager().closeIdleConnections(0);
   }
}
