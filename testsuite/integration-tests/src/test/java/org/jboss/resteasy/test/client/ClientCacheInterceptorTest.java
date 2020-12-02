package org.jboss.resteasy.test.client;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCache;
import org.jboss.resteasy.client.jaxrs.cache.CacheInterceptor;
import org.jboss.resteasy.client.jaxrs.cache.LightweightBrowserCache;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
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
public class ClientCacheInterceptorTest
{

   @XmlRootElement
   public static class Message
   {
      private String message;
      private long createdAt;

      public Message()
      {
         this.createdAt = Instant.now().toEpochMilli();
      }

      public String getMessage()
      {
         return this.message;
      }

      public void setMessage(String message)
      {
         this.message = message;
      }

      public long getCreatedAt()
      {
         return createdAt;
      }

      public void setCreatedAt(long createdAt)
      {
         this.createdAt = createdAt;
      }
   }

   @Path("echo")
   @Produces(value = {XML_WITH_CHARSET, JSON_WITH_CHARSET, TEXT_XML_WITH_CHARSET})
   public static class EchoResource
   {

      @GET
      @Cache(maxAge = 120)
      public Response echo(@QueryParam("msg") String msg)
      {
         Message message = new Message();
         message.setMessage(String.valueOf(msg));
         return Response.ok(message).build();
      }

      @GET
      @NoCache
      @Path("nocache")
      public Response echoNoCache(@QueryParam("msg") String msg)
      {
         Message message = new Message();
         message.setMessage(String.valueOf(msg));
         return Response.ok(message).build();
      }

   }

   private static Client client;
   public static final String XML_WITH_CHARSET = "application/xml;charset=UTF-8";
   public static final String JSON_WITH_CHARSET = "application/json;charset=UTF-8";
   public static final String XML_NO_CHARSET = "application/xml";
   public static final String JSON_NO_CHARSET = "application/json";
   public static final String TEXT_WILDCARD = "text/*";
   public static final String TEXT_XML_WITH_CHARSET = "text/xml;charset=UTF-8";
   public static final String TEXT_XML_NO_CHARSET = "text/xml";
   private static final String DEP = "ClientCacheInterceptorTest";

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(DEP);
      war.addClass(Message.class);
      war.addClass(EchoResource.class);
      return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
   }

   @BeforeClass
   public static void setup()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   private static String generateURL()
   {
      return PortProviderUtil.generateBaseUrl(DEP);
   }

   @Test
   public void testCachedValueNoCache() throws Exception
   {
      BrowserCache cache = new LightweightBrowserCache();
      CacheInterceptor interceptor = new CacheInterceptor(cache);
      final String url = generateURL();
      ClientInvocationBuilder request = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
            .path("nocache").queryParam("msg", "Hello world").request();
      try (ClientResponse response = (ClientResponse) request.get())
      {
         Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
         Assert.assertNull("Cache must not contain any data", cache.getAny(request.getURI().toString()));
      }
   }

   @Test
   public void testCachedValueCharset() throws Exception
   {
      BrowserCache cache = new LightweightBrowserCache();
      CacheInterceptor interceptor = new CacheInterceptor(cache);
      final String url = generateURL();
      ClientInvocationBuilder request = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
            .queryParam("msg", "Hello world").request();
      try (ClientResponse responseA = (ClientResponse) request.accept(XML_NO_CHARSET).get();
            ClientResponse responseB = (ClientResponse) request.accept(XML_NO_CHARSET).get())
      {
         Assert.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
         Assert.assertEquals(Status.OK.getStatusCode(), responseB.getStatus());
         Assert.assertEquals("Content type must be " + XML_WITH_CHARSET, XML_WITH_CHARSET,
               responseA.getHeaderString("Content-Type"));
         Assert.assertEquals("Content type must be " + XML_WITH_CHARSET, XML_WITH_CHARSET,
               responseB.getHeaderString("Content-Type"));
         // assert response body
         String responseAStr = responseA.readEntity(String.class);
         String responseBStr = responseB.readEntity(String.class);
         // if taken from the cache, the createAt epoch must be the same and thus string must be the same as well
         assertEquals("Response entities must be the same", responseAStr, responseBStr);
         Assert.assertNotNull("Cache must contain data", cache.getAny(request.getURI().toString()));
         Assert.assertNotNull("Cache must contain data for the given accepted content type",
               cache.get(request.getURI().toString(), MediaType.APPLICATION_XML_TYPE));
      }
   }

   @Test
   public void testCachedValueWithDifferentAccept() throws Exception
   {
      BrowserCache cache = new LightweightBrowserCache();
      CacheInterceptor interceptor = new CacheInterceptor(cache);
      final String url = generateURL();
      ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
            .queryParam("msg", "Hello world").request();
      ClientInvocationBuilder requestB = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
            .queryParam("msg", "Hello world").request();
      try (ClientResponse responseA = (ClientResponse) requestA.accept(JSON_NO_CHARSET).get();
            ClientResponse responseB = (ClientResponse) requestB.accept(XML_NO_CHARSET).get())
      {
         Assert.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
         Assert.assertEquals(Status.OK.getStatusCode(), responseB.getStatus());
         Assert.assertEquals("Content type must be " + JSON_WITH_CHARSET, JSON_WITH_CHARSET,
               responseA.getHeaderString("Content-Type"));
         Assert.assertEquals("Content type must be " + XML_WITH_CHARSET, XML_WITH_CHARSET,
               responseB.getHeaderString("Content-Type"));
         Assert.assertNotNull("Cache must contain data", cache.getAny(requestA.getURI().toString()));
         // the response must be cached under both types
         Assert.assertNotNull("Cache must contain data for the given accepted content type",
               cache.get(requestA.getURI().toString(), MediaType.APPLICATION_JSON_TYPE));
         Assert.assertNotNull("Cache must contain data for the given accepted content type",
               cache.get(requestB.getURI().toString(), MediaType.APPLICATION_XML_TYPE));
      }
   }

   @Test
   public void testCachedValueWithWildCardAccept() throws Exception
   {
      BrowserCache cache = new LightweightBrowserCache();
      CacheInterceptor interceptor = new CacheInterceptor(cache);
      final String url = generateURL();
      ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
            .queryParam("msg", "Hello world").request();
      // this should produce text/xml since the resource produces text/xml
      try (ClientResponse responseA = (ClientResponse) requestA.accept(TEXT_WILDCARD).get())
      {
         Assert.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
         Assert.assertEquals("Content type must be " + TEXT_XML_WITH_CHARSET, TEXT_XML_WITH_CHARSET,
               responseA.getHeaderString("Content-Type"));
         Assert.assertNotNull("Cache must contain data", cache.getAny(requestA.getURI().toString()));
         // the response must be cached under text/* instead of text/xml
         Assert.assertNotNull("Cache must contain data for the given accepted content type",
               cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_WILDCARD)));
         // since Accept is present, cache entry for media type text/xml (response content type) must not exist
         Assert.assertNull("Cache must contain data for the given accepted content type",
               cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_XML_NO_CHARSET)));
         Assert.assertNull("Cache must contain data for the given accepted content type",
               cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_XML_WITH_CHARSET)));
      }
   }


   @Test
   // Reproduces RESTEASY-2301
   public void testCachedValueWithMultipleAccept() throws Exception
   {
       BrowserCache cache = new LightweightBrowserCache();
       CacheInterceptor interceptor = new CacheInterceptor(cache);
       final String url = generateURL();
       ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
               .queryParam("msg", "Hello world").request();
       try (ClientResponse responseA = (ClientResponse) requestA.accept(JSON_NO_CHARSET, XML_NO_CHARSET + ";q=0.5").get())
      {
         Assert.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
         Assert.assertEquals("Content type must be " + JSON_WITH_CHARSET, JSON_WITH_CHARSET,
                 responseA.getHeaderString("Content-Type"));
         Assert.assertNotNull("Cache must contain data", cache.getAny(requestA.getURI().toString()));

         // the response must be cached as json
         Assert.assertNotNull("Cache must contain data for the given accepted content type",
                 cache.get(requestA.getURI().toString(), MediaType.APPLICATION_JSON_TYPE));

      }
   }

}
