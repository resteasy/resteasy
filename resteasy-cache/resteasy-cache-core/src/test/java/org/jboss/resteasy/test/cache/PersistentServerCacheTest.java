package org.jboss.resteasy.test.cache;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.File;
import java.util.Hashtable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.cache.server.ServerCacheFeature;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1105
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1 $
 * 
 *  * Copyright Mar 25, 2015
 */
public class PersistentServerCacheTest
{
   private static int count = 0;
   private static int plainCount = 0;
   private static int htmlCount = 0;
   
   private static NettyJaxrsServer server;
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("/cache")
   public static class TestResource
   {
      @GET
      @Produces("text/plain")
      @Cache(maxAge = 2)
      public String get()
      {
         count++;
         return "hello world" + count;
      }

      @PUT
      @Consumes("text/plain")
      public void put(String val)
      {
      }

      @GET
      @Produces("text/plain")
      @Path("accepts")
      @Cache(maxAge = 2)
      public String getPlain()
      {
         plainCount++;
         return "plain" + plainCount;
      }

      @GET
      @Produces("text/html")
      @Path("accepts")
      @Cache(maxAge = 2)
      public String getHtml()
      {
         htmlCount++;
         return "html" + htmlCount;
      }

      @GET
      @Produces("text/plain")
      @Path("stuff")
      @Cache(maxAge = 2)
      public String getStuff()
      {
         count++;
         return "stuff";
      }
   }

   @Path("/cache")
   public interface MyProxy
   {
      @GET
      @Produces("text/plain")
      String get();

   }


   @Before
   public void before() throws Exception
   {
      FileUtils.deleteDirectory(new File("target/TestCache"));
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("server.request.cache.infinispan.config.file", "infinispan.xml");
      contextParams.put("server.request.cache.infinispan.cache.name", "TestCache");
      
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      deployment = server.getDeployment();
      
      dispatcher = deployment.getDispatcher();
      deployment.getProviderFactory().property("server.request.cache.infinispan.config.file", "infinispan.xml");
      deployment.getProviderFactory().property("server.request.cache.infinispan.cache.name", "TestCache");
      deployment.getProviderFactory().register(ServerCacheFeature.class);
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      FileUtils.deleteDirectory(new File("target/TestCache"));
      server.stop();
      server = null;
      dispatcher = null;
      deployment = null;
   }
   
   @Test
   public void testNoCacheHitValidation() throws Exception
   {
      // test that after a cache expiration NOT MODIFIED is still returned if matching etags

      count = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/stuff"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "stuff");
      }


      Thread.sleep(2000);

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/stuff"));
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
         Assert.assertEquals(2, count);
      }
   }


   @Test
   public void testCache() throws Exception
   {
      count = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 1);
      }


      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 1);
      }
      // test if-not-match
      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
      }


      Thread.sleep(2000);

      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 2);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 2);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse response = request.body("text/plain", "yo").put();
         Assert.assertEquals(204, response.getStatus());
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 3);
      }
   }


   @Test
   public void testAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }
   }

   @Test
   public void testPreferredAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/html;q=0.5, text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }
   }

   @Test
   public void testPreferredButNotCachedAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      // we test that the preferred can be handled
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getResponseHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }
   }
}