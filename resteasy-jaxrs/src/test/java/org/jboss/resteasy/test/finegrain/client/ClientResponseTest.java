package org.jboss.resteasy.test.finegrain.client;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.ClientRequest;
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
      ClientResponse<Void> getHeader();

      @GET
      @Path("basic")
      ClientResponse<byte[]> getBasicBytes();

      @GET
      @Path("error")
      ClientResponse<String> getError();
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

   @SuppressWarnings("unchecked")
   @Test
   public void testClientResponse() throws Exception
   {
      Client client = ProxyFactory
            .create(Client.class, "http://localhost:8081");

      Assert.assertEquals("basic", client.getBasic().getEntity());
      client.putBasic("hello world");
      Assert.assertEquals("hello world", client.getQueryParam("hello world")
            .getEntity());

      String queryResult = new ClientRequest("http://localhost:8081/queryParam")
            .queryParameter("param", "hello world").get(String.class).getEntity();
      Assert.assertEquals("hello world", queryResult);

      Assert
            .assertEquals(1234, client.getUriParam(1234).getEntity().intValue());

      ClientResponse<Integer> paramPathResult = new ClientRequest(
            "http://localhost:8081/uriParam/{param}").accept("text/plain")
            .pathParameter("param", 1234).get(Integer.class);
      Assert.assertEquals(1234, paramPathResult.getEntity().intValue());

      Assert.assertEquals(Response.Status.OK, client
            .putBasicReturnCode("hello world"));
      ClientResponse putResponse = new ClientRequest(
            "http://localhost:8081/basic").body("text/plain", "hello world").put();

      Assert.assertEquals(Response.Status.OK, putResponse.getResponseStatus());

      Assert.assertEquals("headervalue", client.getHeader().getHeaders()
            .getFirst("header"));
      ClientResponse getHeaderResponse = new ClientRequest(
            "http://localhost:8081/header").get();
      Assert.assertEquals("headervalue", getHeaderResponse.getHeaders()
            .getFirst("header"));

      final byte[] entity = client.getBasicBytes().getEntity();
      Assert.assertTrue(Arrays.equals("basic".getBytes(), entity));

      ClientResponse<byte[]> getBasicResponse = new ClientRequest(
         "http://localhost:8081/basic").get(byte[].class);
      Assert.assertTrue(Arrays.equals("basic".getBytes(), getBasicResponse.getEntity()));

      Assert.assertEquals("basic", client.getBasic2().getEntity(String.class, null));

      getBasicResponse = new ClientRequest(
         "http://localhost:8081/basic").get(byte[].class);
      Assert.assertEquals("basic", getBasicResponse.getEntity(String.class, null));
   }

   @Test
   public void testErrorResponse() throws Exception
   {
      Client client = null;
      client = ProxyFactory.create(Client.class, "http://localhost:8081/shite");
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
            return Response.seeOther(
                  new URI("http://localhost:8081/redirect/data")).build();
         }
         catch (URISyntaxException e)
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
         testRedirect(ProxyFactory.create(RedirectClient.class, "http://localhost:8081").get());
         testRedirect(new ClientRequest("http://localhost:8081/redirect").get());
      }
      System.out.println("*****");
      {
         URL url = new URL("http://localhost:8081/redirect");
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
         System.out.println(":"
               + response.getHeaders().getFirst(name.toString()));
      }
      Assert.assertEquals((String) response.getHeaders().getFirst("location"), "http://localhost:8081/redirect/data");
   }

}