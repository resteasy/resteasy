package org.jboss.resteasy.test.regression;

import org.w3c.dom.Document;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.*;
import static org.jboss.resteasy.util.HttpClient4xUtils.consumeEntity;

/**
 * A basket of JIRA regression tests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionBasketTest extends BaseResourceTest
{
   @Path("/inputstream")
   public static class MyTest
   {
      @POST
      @Path("/test/{type}")
      public void test(InputStream is, @PathParam("type") final String type) throws IOException
      {

      }


   }

   @Path("/{api:(?i:api)}")
   public static class Api
   {
      @Path("/{func:(?i:func)}")
      @GET
      @Produces("text/plain")
      public String func()
      {
         return "hello";
      }

      @PUT
      public void put(@Context HttpHeaders headers, String val)
      {
         System.out.println(headers.getMediaType());
         Assert.assertEquals(val, "hello");
      }
   }

   @Path("/delete")
   public static class DeleteTest
   {
      @DELETE
      @Consumes("text/plain")
      public void delete(String msg)
      {
         Assert.assertEquals("hello", msg);
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(MyTest.class);
      addPerRequestResource(Api.class);
      addPerRequestResource(DeleteTest.class);
   }

   @Test
   public void test631() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/delete"));
      ClientResponse response = request.body("text/plain", "hello").delete();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void test534() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/inputstream/test/json"));
      request.body(MediaType.APPLICATION_OCTET_STREAM, "hello world".getBytes());
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void test624() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/ApI/FuNc"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();

   }

   @Test
   public void test583() throws Exception
   {
      HttpClient client = new DefaultHttpClient();
      HttpPut method = new HttpPut(generateURL("/api"));
      HttpResponse response = null;
      try
      {
         method.setEntity(new StringEntity("hello", "vnd.net.juniper.space.target-management.targets+xml;version=1;charset=UTF-8", null));
         response = client.execute(method);
         Assert.assertEquals(response.getStatusLine().getStatusCode(), 400);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         consumeEntity(response);
      }

   }

   private static class SubFactory extends ResteasyProviderFactory
   {
      public MediaTypeMap<SortedKey<MessageBodyReader>> getMBRMap() { return messageBodyReaders; }

   }

   @Test
   public void test638() throws Exception
   {
      SubFactory factory = new SubFactory();
      RegisterBuiltin.register(factory);

      for (int i = 0; i < 10; i++)
      {
         MediaType type = MediaType.valueOf("text/xml; boundary=" + i);
         Assert.assertTrue(factory.getMBRMap().getPossible(type, Document.class).size() > 1);
      }

      System.out.println("cache size: " + factory.getMBRMap().getClassCache().size());
      Assert.assertEquals(1, factory.getMBRMap().getClassCache().size());

   }
}
