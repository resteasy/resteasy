package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProduceConsumeTest extends BaseResourceTest
{
   public static class Data
   {
      public String data;
      public String type;

      public Data(String data, String type)
      {
         this.data = data;
         this.type = type;
      }

      @Override
      public String toString()
      {
         return "Data{" +
                 "data='" + data + '\'' +
                 ", type='" + type + '\'' +
                 '}';
      }
   }

   @Path("resource")
   public static class Resource {

      @POST
      @Path("plain")
      @Produces(MediaType.TEXT_PLAIN)
      public String postPlain()
      {
         return MediaType.TEXT_PLAIN;
      }

      @POST
      @Path("wild")
      public Data data(Data data)
      {
         System.out.println("HERE!: " + data);
         return data;
      }

      @Path("empty")
      @GET
      public Response entity() {
         return Response.ok().build();
      }


   }

   public static class WildData implements MessageBodyReader<Data>, MessageBodyWriter<Data>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public Data readFrom(Class<Data> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         String str = ProviderHelper.readString(entityStream, mediaType);
         return new Data(str, "wild");
      }

      @Override
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public long getSize(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      @Override
      public void writeTo(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         String str = data.data + ":" + data.type + ":wild";
         entityStream.write(str.getBytes());
      }
   }

   @Produces("text/plain")
   @Consumes("text/plain")
   public static class TextData implements MessageBodyReader<Data>, MessageBodyWriter<Data>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public Data readFrom(Class<Data> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         String str = ProviderHelper.readString(entityStream, mediaType);
         return new Data(str, "text");
      }

      @Override
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Data.class);
      }

      @Override
      public long getSize(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      @Override
      public void writeTo(Data data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         String str = data.data + ":" + data.type + ":text";
         entityStream.write(str.getBytes());
      }
   }



   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(TextData.class);
      deployment.getProviderFactory().register(WildData.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testEmpty()
   {
      Response response = client.target(generateURL("/resource/empty")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
              MediaType.TEXT_PLAIN_TYPE);
      try
      {
         BigDecimal big = response.readEntity(BigDecimal.class);
         Assert.fail();
      }
      catch (ProcessingException e)
      {
         Assert.assertTrue(e.getCause() instanceof NoContentException);
      }

   }

   @Test
   public void testEmptyCharacter()
   {
      Response response = client.target(generateURL("/resource/empty")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
              MediaType.TEXT_PLAIN_TYPE);
      try
      {
         Character big = response.readEntity(Character.class);
         Assert.fail();
      }
      catch (ProcessingException e)
      {
         Assert.assertTrue(e.getCause() instanceof NoContentException);
      }

   }

   @Test
   public void testEmptyInteger()
   {
      Response response = client.target(generateURL("/resource/empty")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
              MediaType.TEXT_PLAIN_TYPE);
      try
      {
         Integer big = response.readEntity(Integer.class);
         Assert.fail();
      }
      catch (ProcessingException e)
      {
         Assert.assertTrue(e.getCause() instanceof NoContentException);
      }

   }


   @Test
   public void testEmptyForm()
   {
      Response response = client.target(generateURL("/resource/empty")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.getHeaders().add(HttpHeaders.CONTENT_TYPE,
              MediaType.APPLICATION_FORM_URLENCODED);
      MultivaluedMap big = response.readEntity(MultivaluedMap.class);

      Assert.assertTrue(big == null || big.size() == 0);

   }



   @Test
   public void testWild()
   {
      Response response = client.target(generateURL("/resource/wild")).request("*/*").post(Entity.entity("data", MediaType.WILDCARD_TYPE));
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println("----");
      System.out.println(response.readEntity(String.class));
      response.close();
   }

}
