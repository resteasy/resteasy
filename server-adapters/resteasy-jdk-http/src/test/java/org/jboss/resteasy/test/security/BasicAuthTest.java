package org.jboss.resteasy.test.security;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.embedded.SimpleSecurityDomain;
import org.jboss.resteasy.plugins.server.sun.http.HttpServerContainer;
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
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthTest
{
   private static final Logger LOG = Logger.getLogger(BasicAuthTest.class);
   private static Dispatcher dispatcher;

   @Path("/secured")
   public interface BaseProxy
   {
      @GET
      String get();

      @GET
      @Path("/authorized")
      String getAuthorized();

      @GET
      @Path("/deny")
      String deny();

      @GET
      @Path("/failure")
      List<String> getFailure();
   }

   @Path("/secured")
   public static class BaseResource
   {
      @GET
      @Path("/failure")
      @RolesAllowed("admin")
      public List<String> getFailure()
      {
         return null;
      }

      @GET
      public String get(@Context SecurityContext ctx)
      {
         LOG.info("********* IN SECURE CLIENT");
         if (!ctx.isUserInRole("admin"))
         {
            LOG.info("NOT IN ROLE!!!!");
            throw new WebApplicationException(403);
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

   @Path("/secured2")
   public static class BaseResource2
   {
      public String get(@Context SecurityContext ctx)
      {
         LOG.info("********* IN SECURE CLIENT");
         if (!ctx.isUserInRole("admin"))
         {
            LOG.info("NOT IN ROLE!!!!");
            throw new WebApplicationException(403);
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
      dispatcher = HttpServerContainer.start("", domain).getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
      dispatcher.getRegistry().addPerRequestResource(BaseResource2.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      HttpServerContainer.stop();
   }

   @Test
   public void testProxy() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password");
      client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), credentials);
      ClientExecutor executor = createAuthenticatingExecutor(client);
      BaseProxy proxy = ProxyFactory.create(BaseProxy.class, generateURL(""), executor);
      String val = proxy.get();
      Assert.assertEquals(val, "hello");
      val = proxy.getAuthorized();
      Assert.assertEquals(val, "authorized");
   }

   @Test
   public void testProxyFailure() throws Exception
   {
      BaseProxy proxy = ProxyFactory.create(BaseProxy.class, generateURL(""));
      try
      {
         proxy.getFailure();
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 401);
      }
   }

   @Test
   public void testSecurity() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password");
      client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), credentials);
      ClientExecutor executor = createAuthenticatingExecutor(client);
 
      {
         ClientRequest request = new ClientRequest(generateURL("/secured"), executor);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("hello", response.getEntity());
      }
      
      {
         ClientRequest request = new ClientRequest(generateURL("/secured/authorized"), executor);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("authorized", response.getEntity());  
      }
      
      {
         ClientRequest request = new ClientRequest(generateURL("/secured/deny"), executor);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
      }
   }

   /**
    * RESTEASY-579
    *
    * Found 579 bug when doing 575 so the test is here out of laziness
    *
    * @throws Exception
    */
   @Test
   public void test579() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password");
      client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), credentials);
      ClientExecutor executor = createAuthenticatingExecutor(client);
      ClientRequest request = new ClientRequest(generateURL("/secured2"), executor);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(404, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testSecurityFailure() throws Exception
   {
      DefaultHttpClient client = new DefaultHttpClient();

      {
         HttpGet method = new HttpGet(generateURL("/secured"));
         HttpResponse response = client.execute(method);
         Assert.assertEquals(401, response.getStatusLine().getStatusCode());
         EntityUtils.consume(response.getEntity());
      }

      ClientExecutor executor = createAuthenticatingExecutor(client);
      
      {
         UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password");
         client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), credentials);
         ClientRequest request = new ClientRequest(generateURL("/secured/authorized"), executor);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("authorized", response.getEntity());  
      }
      
      {
         UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("mo", "password");
         client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY), credentials);
         ClientRequest request = new ClientRequest(generateURL("/secured/authorized"), executor);
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
         response.releaseConnection();
      }
   }

   /**
    * Create a ClientExecutor which does preemptive authentication.
    */
   
   static private ClientExecutor createAuthenticatingExecutor(DefaultHttpClient client)
   {
      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();
      
      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuth = new BasicScheme();
      HttpHost targetHost = new HttpHost("localhost", 8081);
      authCache.put(targetHost, basicAuth);

      // Add AuthCache to the execution context
      BasicHttpContext localContext = new BasicHttpContext();
      localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
      
      // Create ClientExecutor.
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client, localContext);
      return executor;
   }
}
