package org.jboss.resteasy.test.finegrain.client;

import static org.jboss.resteasy.test.TestPortProvider.createClientRequest;
import static org.jboss.resteasy.test.TestPortProvider.createProxy;
import static org.jboss.resteasy.test.TestPortProvider.createURI;
import static org.jboss.resteasy.test.TestPortProvider.createURL;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.smoke.SimpleResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ClientResponseTest
{

   private static Dispatcher dispatcher;

   @Path("/")
   public interface Client
   {
      @GET
      @Path("basic")
      @Produces("text/plain")
      ClientResponse<String> getBasic();

      @GET
      @Path("basic")
      @ClientResponseType(entityType = String.class)
      Response getBasicResponseString();

      @GET
      @Path("basic")
      @ClientResponseType(entityTypeFactory = StringEntityTypeFactory.class)
      Response getBasicResponseStringFactory();

      @GET
      String getData(@ClientURI String uri);
      
      @PUT
      @Consumes("text/plain")
      Response.Status putData(@ClientURI URI uri, String data);

      @GET
      @Path("basic")
      ClientResponse getBasic2();

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      void putBasic(String body);

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      Response.Status putBasicReturnCode(String body);

      @GET
      @Path("queryParam")
      @Produces("text/plain")
      ClientResponse<String> getQueryParam(@QueryParam("param") String param);

      @GET
      @Path("uriParam/{param}")
      @Produces("text/plain")
      ClientResponse<Integer> getUriParam(@PathParam("param") int param);

      @GET
      @Path("header")
      ClientResponse<Void> getHeaderClientResponse();

      @GET
      @Path("header")
      Response getHeaderResponse();

      @GET
      @Path("basic")
      ClientResponse<byte[]> getBasicBytes();

      @GET
      @Path("basic")
      @ClientResponseType(entityType = byte[].class)
      Response getBasicResponse();

      @GET
      @Path("error")
      ClientResponse<String> getError();
   }

   public static class StringEntityTypeFactory implements EntityTypeFactory
   {

      public Class getEntityType(int status,
                                 MultivaluedMap<String, Object> metadata)
      {
         return String.class;
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testClientResponse() throws Exception
   {
      Client client = ProxyFactory.create(Client.class, generateBaseUrl());

      Assert.assertEquals("basic", client.getBasic().getEntity());
      Assert.assertEquals("basic", client.getBasicResponseString().getEntity());
      Assert.assertEquals("basic", client.getBasicResponseStringFactory().getEntity());
      Assert.assertEquals("basic", client.getData("/basic"));
      client.putBasic("hello world");
      Assert.assertEquals("hello world", client.getQueryParam("hello world").getEntity());

      client.putData(new URI("/basic"), "hello world2");
      Assert.assertEquals("hello world", client.getQueryParam("hello world").getEntity());

      String queryResult = createClientRequest("/queryParam").queryParameter("param", "hello world").get(String.class)
              .getEntity();
      Assert.assertEquals("hello world", queryResult);

      Assert.assertEquals(1234, client.getUriParam(1234).getEntity().intValue());

      ClientResponse<Integer> paramPathResult = createClientRequest("/uriParam/{param}").accept("text/plain")
              .pathParameter("param", 1234).get(Integer.class);
      Assert.assertEquals(1234, paramPathResult.getEntity().intValue());

      Assert.assertEquals(Response.Status.NO_CONTENT, client.putBasicReturnCode("hello world"));
      ClientResponse putResponse = createClientRequest("/basic").body("text/plain", "hello world").put();

      Assert.assertEquals(Response.Status.NO_CONTENT, putResponse.getResponseStatus());

      Assert.assertEquals("headervalue", ((BaseClientResponse) client.getHeaderClientResponse()).getHeaders().getFirst("header"));
      Assert.assertEquals("headervalue", createClientRequest("/header").get().getHeaders().getFirst("header"));
      Assert.assertEquals("headervalue", client.getHeaderResponse().getMetadata().getFirst("header"));

      final byte[] entity = client.getBasicBytes().getEntity();
      Assert.assertTrue(Arrays.equals("basic".getBytes(), entity));
      Assert.assertTrue(Arrays.equals("basic".getBytes(), (byte[]) client.getBasicResponse().getEntity()));

      ClientResponse<byte[]> getBasicResponse = createClientRequest("/basic").get(byte[].class);
      Assert.assertTrue(Arrays.equals("basic".getBytes(), getBasicResponse.getEntity()));


      Assert.assertEquals("basic", client.getBasic2().getEntity(String.class, null));

      getBasicResponse = createClientRequest("/basic").get(byte[].class);
      Assert.assertEquals("basic", getBasicResponse.getEntity(String.class, null));
   }

   @Test
   public void testErrorResponse() throws Exception
   {
      Client client = null;
      client = createProxy(Client.class, "/shite");
      ClientResponse<String> response = client.getBasic();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      response = client.getError();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());

   }

   @Path("/redirect")
   public static class RedirectResource
   {
      @GET
      public Response get()
      {
         try
         {
            return Response.seeOther(createURI("/redirect/data")).build();
         }
         catch (IllegalArgumentException e)
         {
            throw new RuntimeException(e);
         }
      }

      @GET
      @Path("data")
      public String getData()
      {
         return "data";
      }
   }

   @Path("/redirect")
   public static interface RedirectClient
   {
      @GET
      ClientResponse get();
   }

   @Test
   public void testRedirect() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(RedirectResource.class);
      {
         testRedirect(ProxyFactory.create(RedirectClient.class, generateBaseUrl()).get());
         testRedirect(createClientRequest("/redirect").get());
      }
      System.out.println("*****");
      {
         URL url = createURL("/redirect");
         // HttpURLConnection.setFollowRedirects(false);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setInstanceFollowRedirects(false);
         conn.setRequestMethod("GET");
         Map headers = conn.getHeaderFields();
         for (Object name : headers.keySet())
         {
            System.out.println(name);
         }
         System.out.println(conn.getResponseCode());
      }

   }

   private void testRedirect(ClientResponse response)
   {
      System.out.println("size: " + response.getHeaders().size());
      for (Object name : response.getHeaders().keySet())
      {
         System.out.print(name);
         System.out.println(":" + response.getHeaders().getFirst(name.toString()));
      }
      Assert.assertEquals((String) response.getHeaders().getFirst("location"), generateURL("/redirect/data"));
   }

}