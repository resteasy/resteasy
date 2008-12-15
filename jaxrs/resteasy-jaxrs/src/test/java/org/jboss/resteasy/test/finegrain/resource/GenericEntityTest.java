package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GenericEntityTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class GenericResource
   {
      @Path("floats")
      @GET
      public Response getFloats()
      {
         ArrayList<Float> list = new ArrayList<Float>();
         list.add(45.0f);
         list.add(50.0f);
         GenericEntity<List<Float>> ge = new GenericEntity<List<Float>>(list)
         {
         };
         return Response.ok(ge).build();
      }

      @Path("doubles")
      @GET
      public Response getDoubles()
      {
         ArrayList<Double> list = new ArrayList<Double>();
         list.add(45.0);
         list.add(50.0);
         GenericEntity<List<Double>> ge = new GenericEntity<List<Double>>(list)
         {
         };
         return Response.ok(ge).build();
      }
   }

   @Provider
   @Produces("*/*")
   public static class FloatWriter implements MessageBodyWriter<List<Float>>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         if (!List.class.isAssignableFrom(type))
            return false;
         if (!(genericType instanceof ParameterizedType))
            return false;
         ParameterizedType pt = (ParameterizedType) genericType;
         boolean result = pt.getActualTypeArguments()[0].equals(Float.class);
         System.out.println("FloatWriter result!!!: " + result);
         return result;
      }

      public long getSize(List<Float> floats, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
      {
         return -1;
      }

      public void writeTo(List<Float> floats, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
      {
         StringBuffer buf = new StringBuffer();
         for (Float f : floats)
         {
            buf.append(f.toString()).append("F ");
         }
         entityStream.write(buf.toString().getBytes());
      }
   }

   @Provider
   @Produces("*/*")
   public static class DoubleWriter implements MessageBodyWriter<List<Double>>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         System.out.println("DoubleWriter type: " + type.getName());
         if (!List.class.isAssignableFrom(type))
            return false;
         System.out.println("DoubleWriter: " + genericType);
         if (!(genericType instanceof ParameterizedType))
            return false;
         System.out.println("DoubleWriter");
         ParameterizedType pt = (ParameterizedType) genericType;
         boolean result = pt.getActualTypeArguments()[0].equals(Double.class);
         System.out.println("Doublewriter result!!!: " + result);
         return result;
      }

      public long getSize(List<Double> doubles, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
      {
         return -1;
      }

      public void writeTo(List<Double> floats, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
      {
         StringBuffer buf = new StringBuffer();
         for (Double f : floats)
         {
            buf.append(f.toString()).append("D ");
         }
         entityStream.write(buf.toString().getBytes());
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(GenericResource.class);
      ResteasyProviderFactory.getInstance().addMessageBodyWriter(DoubleWriter.class);
      ResteasyProviderFactory.getInstance().addMessageBodyWriter(FloatWriter.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testDoubles()
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/doubles");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         String result = method.getResponseBodyAsString();
         Assert.assertEquals("45.0D 50.0D ", result);
         System.out.println(result);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testFloats()
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/floats");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         String result = method.getResponseBodyAsString();
         Assert.assertEquals("45.0F 50.0F ", result);
         System.out.println(result);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

}