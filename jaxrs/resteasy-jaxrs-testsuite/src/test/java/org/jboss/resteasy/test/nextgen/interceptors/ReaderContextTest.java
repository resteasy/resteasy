package org.jboss.resteasy.test.nextgen.interceptors;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderContextTest extends BaseResourceTest
{
   public static final//
   String readFromStream(InputStream stream) throws IOException {
      InputStreamReader isr = new InputStreamReader(stream);
      return readFromReader(isr);
   }

   public static final//
   String readFromReader(Reader reader) throws IOException {
      BufferedReader br = new BufferedReader(reader);
      String entity = br.readLine();
      br.close();
      return entity;
   }

   @Path("resource")
   public static class Resource {

      public static final String HEADERNAME = "FILTER_HEADER";

      public static final String getName() {
         // make this long enough to let entity provider getSize()
         // be enough to let our interceptor name fit in
         return "<resource>" + Resource.class.getName() + "</resource>";
      }

      @POST
      @Path("postlist")
      public String postList(List<String> list) {
         return list.iterator().next();
      }

      @GET
      @Path("getlist")
      public Response getList() {
         ArrayList<String> list = new ArrayList<String>();
         list.add(getName());
         GenericEntity<ArrayList<String>> entity = new GenericEntity<ArrayList<String>>(
                 list) {
         };
         return buildResponse(entity);
      }

      @POST
      @Path("poststring")
      public Response postString(String string) {
         return buildResponse(string);
      }

      // ///////////////////////////////////////////////////////////////////////////
      // Send header that would have the power to enable filter / interceptor
      // The header is passed from client request
      @Context
      private HttpHeaders headers;

      private Response buildResponse(Object content) {
         return buildResponse(content, MediaType.WILDCARD_TYPE);
      }

      private Response buildResponse(Object content, MediaType type) {
         List<String> list = headers.getRequestHeader(HEADERNAME);
         String name = null;
         if (list != null && list.size() != 0)
            name = list.iterator().next();
         Response.ResponseBuilder builder = Response.ok(content, type).type(type);
         if (name != null)
            builder.header(HEADERNAME, name);
         return builder.build();
      }

   }

   @Provider
   public static class ArrayListEntityProvider implements
           MessageBodyReader<ArrayList<String>>,
           MessageBodyWriter<ArrayList<String>>
   {

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return type == ArrayList.class;
      }

      @Override
      public long getSize(ArrayList<String> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         int annlen = annotations.length > 0 ? annotations[0].annotationType()
                 .getName().length() : 0;
         return t.iterator().next().length() + annlen
                 + mediaType.toString().length();
      }

      @Override
      public void writeTo(ArrayList<String> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         String ann = "";
         if (annotations.length > 0)
            ann = annotations[0].annotationType().getName();
         entityStream.write((t.iterator().next() + ann + mediaType.toString())
                 .getBytes());
      }

      @Override
      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType) {
         return type == ArrayList.class;
      }

      @Override
      public ArrayList<String> readFrom(Class<ArrayList<String>> type,
                                        Type genericType, Annotation[] annotations, MediaType mediaType,
                                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
              throws IOException, WebApplicationException {
         String text = readFromStream(entityStream);
         entityStream.close();
         String ann = "";
         if (annotations.length > 0)
            ann = annotations[0].annotationType().getName();
         ArrayList<String> list = new ArrayList<String>();
         list.add(text + ann + mediaType.toString());
         return list;
      }
   }

   @Provider
   @Priority(100)
   public static class FirstReaderInterceptor implements ReaderInterceptor {

      @Override
      public Object aroundReadFrom(ReaderInterceptorContext context)
              throws IOException, WebApplicationException {
         MultivaluedMap<String, String> headers = context.getHeaders();
         String header = headers.getFirst(Resource.HEADERNAME);
         if (header != null && header.equals(getClass().getName())) {
            context.setAnnotations(Resource.class.getAnnotations());
            context.setInputStream(new ByteArrayInputStream(getClass()
                    .getName().getBytes()));
            context.setMediaType(MediaType.TEXT_HTML_TYPE);
            context.setType(LinkedList.class);
         }
         return context.proceed();
      }

   }

   @Provider
   @Priority(100)
   public static class FirstWriterInterceptor implements WriterInterceptor {

      @Override
      public void aroundWriteTo(WriterInterceptorContext context)
              throws IOException, WebApplicationException {
         MultivaluedMap<String, Object> headers = context.getHeaders();
         String header = (String) headers.getFirst(Resource.HEADERNAME);
         if (header != null && header.equals(getClass().getName())) {
            context.setAnnotations(Resource.class.getAnnotations());
            context.setEntity(toList(getClass().getName()));
            context.setMediaType(MediaType.TEXT_HTML_TYPE);
            context.setType(LinkedList.class);
         }
         context.proceed();
      }

      private static <T> LinkedList<T> toList(T o){
         LinkedList<T> list = new LinkedList<T>();
         list.add(o);
         return list;
      }
   }

   @Provider
   public static class LinkedListEntityProvider implements
           MessageBodyReader<LinkedList<String>>,
           MessageBodyWriter<LinkedList<String>> {

      public static final String ERROR = "This LinkedList provider should never be used";

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return type == LinkedList.class;
      }

      @Override
      public long getSize(LinkedList<String> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType) {
         return ERROR.length();
      }

      @Override
      public void writeTo(LinkedList<String> t, Class<?> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         entityStream.write(ERROR.getBytes());
      }

      @Override
      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType) {
         return isWriteable(type, genericType, annotations, mediaType);
      }

      @Override
      public LinkedList<String> readFrom(Class<LinkedList<String>> type,
                                         Type genericType, Annotation[] annotations, MediaType mediaType,
                                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
              throws IOException, WebApplicationException {
         LinkedList<String> list = new LinkedList<String>();
         list.add(ERROR);
         return list;
      }

   }

   @Priority(200)
   public static class SecondReaderInterceptor implements ReaderInterceptor {

      @Override
      public Object aroundReadFrom(ReaderInterceptorContext context)
              throws IOException, WebApplicationException {
         MultivaluedMap<String, String> headers = context.getHeaders();
         String header = headers.getFirst(Resource.HEADERNAME);
         if (header != null
                 && header.equals(FirstReaderInterceptor.class.getName())) {
            context.setAnnotations(getClass().getAnnotations());
            context.setInputStream(new ByteArrayInputStream(getClass()
                    .getName().getBytes()));
            context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
            context.setType(ArrayList.class);
         }
         return context.proceed();
      }
   }

   @Priority(200)
   public static class SecondWriterInterceptor implements WriterInterceptor {

      @Override
      public void aroundWriteTo(WriterInterceptorContext context)
              throws IOException, WebApplicationException {
         MultivaluedMap<String, Object> headers = context.getHeaders();
         String header = (String) headers.getFirst(Resource.HEADERNAME);
         if (header != null
                 && header.equals(FirstWriterInterceptor.class.getName())) {
            context.setAnnotations(getClass().getAnnotations());
            context.setEntity(toList(getClass().getName()));
            context.setGenericType(String.class);
            context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
            context.setType(ArrayList.class);
         }
         context.proceed();
      }

      private static <T> ArrayList<T> toList(T o){
         ArrayList<T> list = new ArrayList<T>();
         list.add(o);
         return list;
      }

   }






   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(ArrayListEntityProvider.class);
      deployment.getProviderFactory().register(LinkedListEntityProvider.class);
      deployment.getProviderFactory().register(FirstReaderInterceptor.class);
      deployment.getProviderFactory().register(FirstWriterInterceptor.class);
      deployment.getProviderFactory().register(SecondReaderInterceptor.class);
      deployment.getProviderFactory().register(SecondWriterInterceptor.class);

      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void readerContextOnClientTest()
   {
      WebTarget target = client.target(generateURL("/resource/poststring"));
      target.register(FirstReaderInterceptor.class);
      target.register(SecondReaderInterceptor.class);
      target.register(ArrayListEntityProvider.class);
      target.register(LinkedListEntityProvider.class);
      Response response = target.request().post(Entity.text("plaintext"));
      response.getHeaders().add(Resource.HEADERNAME,
              FirstReaderInterceptor.class.getName());
      @SuppressWarnings("unchecked")
      List<String> list = response.readEntity(List.class);
      Assert.assertTrue(ArrayList.class.isInstance(list));
      String entity = list.get(0);
      Assert.assertTrue(entity.contains(SecondReaderInterceptor.class.getName()));
      Assert.assertTrue(entity.contains(SecondReaderInterceptor.class.getAnnotations()[0]
                      .annotationType().getName()));
   }
}
