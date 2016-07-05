package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CollectionProviderTest extends BaseResourceTest
{
   public static String getPathValue(Annotation[] annotations) {
      return getSpecifiedAnnotationValue(annotations, Path.class);
   }
   @SuppressWarnings("unchecked")
   public static <T extends Annotation> T getSpecifiedAnnotation(
           Annotation[] annotations, Class<T> clazz) {
      T t = null;
      for (Annotation a : annotations)
         if (a.annotationType() == clazz)
            t = (T) a;
      return t != null ? t : null;
   }

   public static <T extends Annotation> String getSpecifiedAnnotationValue(
           Annotation[] annotations, Class<T> clazz) {
      T t = getSpecifiedAnnotation(annotations, clazz);
      try {
         Method m = clazz.getMethod("value");
         return (String) m.invoke(t);
      } catch (Exception e) {
         return null;
      }
   }

   private static boolean checkOther(Class<?> type, Type genericType) {
      if (!(genericType instanceof ParameterizedType))
         return false;
      ParameterizedType pType = (ParameterizedType) genericType;
      boolean ok = pType.getRawType().equals(LinkedList.class);
      ok &= pType.getActualTypeArguments()[0].equals(String.class);
      return ok;
   }

   private static boolean checkResponseNongeneric(Class<?> type,
                                                  Type genericType) {
      boolean ok = genericType.equals(LinkedList.class);
      ok &= type.equals(LinkedList.class);
      return ok;
   }

   private static boolean checkGeneric(Class<?> type, Type genericType) {
      if (ParameterizedType.class.isInstance(genericType))
         genericType = ((ParameterizedType) genericType).getRawType();
      boolean ok = genericType.getClass().equals(List.class)
              || genericType.equals(LinkedList.class);
      ok &= type.equals(LinkedList.class);
      return ok;
   }

   @Provider
   public static class CollectionWriter implements
           MessageBodyWriter<Collection<?>>
   {
      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         String path = getPathValue(annotations);
         // Return type : Other
         if (path.equalsIgnoreCase(type.getSimpleName()))
            return checkOther(type, genericType);
         else if (path.equalsIgnoreCase("response/linkedlist"))
            return checkResponseNongeneric(type, genericType);
         else if (path.equalsIgnoreCase("response/genericentity/linkedlist"))
            return checkGeneric(type, genericType);
         else if (path.equalsIgnoreCase("genericentity/linkedlist"))
            return checkGeneric(type, genericType);
         return false;
      }

      @Override
      public long getSize(Collection<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         return Response.Status.OK.name().length();
      }

      @Override
      public void writeTo(Collection<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException
      {
         entityStream.write(Response.Status.OK.name().getBytes());
      }

   }

   @Provider
   public static class IncorrectCollectionWriter
           implements MessageBodyWriter<Collection<?>> {

      public static final String ERROR = "ERROR ";

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return !new CollectionWriter().isWriteable(type, genericType,
                 annotations, mediaType);
      }

      @Override
      public long getSize(Collection<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         String path = getPathValue(annotations);
         return ERROR.length() + path.length();
      }

      @Override
      public void writeTo(Collection<?> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         String path = getPathValue(annotations);
         entityStream.write(ERROR.getBytes());
         entityStream.write(path.getBytes());
      }
   }

   @Path("resource")
   public static class Resource {

      @Path("linkedlist")
      @GET
      public LinkedList<String> checkDirect() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("linked");
         list.add("list");
         return list;
      }

      @Path("response/linkedlist")
      @GET
      public Response checkResponseDirect() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("linked");
         list.add("list");
         return Response.ok(list).build();
      }

      @Path("response/genericentity/linkedlist")
      @GET
      public Response checkResponseGeneric() {
         GenericEntity<LinkedList<String>> gells = checkGeneric();
         return Response.ok(gells).build();
      }

      @Path("genericentity/linkedlist")
      @GET
      public GenericEntity<LinkedList<String>> checkGeneric() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("linked");
         list.add("list");
         GenericEntity<LinkedList<String>> gells;
         gells = new GenericEntity<LinkedList<String>>(list, getMethodByName(
                 "checkDirect").getGenericReturnType());
         return gells;
      }

      private Method getMethodByName(String name) {
         try {
            Method method = getClass().getMethod(name);
            return method;
         } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(CollectionWriter.class);
      deployment.getProviderFactory().register(IncorrectCollectionWriter.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testGenericTypeDefault()
   {
       Response response = client.target(generateURL("/resource/response/linkedlist")).request().get();
       String val = response.readEntity(String.class);
       System.out.println(val);
      Assert.assertEquals("OK", val);
   }

   @Test
   public void testGenericTypeResponse()
   {
      Response response = client.target(generateURL("/resource/genericentity/linkedlist")).request().get();
      String val = response.readEntity(String.class);
      System.out.println(val);
      Assert.assertEquals("OK", val);
   }



}
