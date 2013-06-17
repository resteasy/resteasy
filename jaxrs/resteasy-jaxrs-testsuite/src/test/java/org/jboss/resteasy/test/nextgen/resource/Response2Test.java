package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Response2Test extends BaseResourceTest
{

   @Provider
   @Consumes
/**
 * This is the dummy class to get annotations from it
 */
   public abstract class AnnotatedClass {

   }
   @Path("/")
   public static class Resource {
      @POST
      @Path("entity")
      public Response entity(Date date) {
         Annotation[] annotations = AnnotatedClass.class.getAnnotations();
         Response response = Response.ok().entity(date, annotations).build();
         return response;
      }

   }

   @Provider
   @Produces("*/*")
   public static class DateContainerReaderWriter implements MessageBodyReader<Date>,
           MessageBodyWriter<Date> {

      public static final int ANNOTATION_NONE = 0;
      public static final int ANNOTATION_CONSUMES = 1 << 2;
      public static final int ANNOTATION_PROVIDER = 1 << 3;
      public static final int ANNOTATION_UNKNOWN = 1 << 7;
      public static final String SPLITTER = " ANNOTATION_VALUE ";

      @Override
      public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4) {
         Annotation[] annotations = AnnotatedClass.class.getAnnotations();
         int size = String.valueOf(Long.MAX_VALUE).length() + SPLITTER.length()
                 + annotations[0].annotationType().getName().length()
                 + annotations[1].annotationType().getName().length();
         System.out.println("getSize() " + size);
         return size;
      }

      @Override
      public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                 MediaType arg3) {
         return arg0 == Date.class;
      }

      @Override
      public void writeTo(Date date, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4, MultivaluedMap<String, Object> arg5,
                          OutputStream stream) throws IOException, WebApplicationException {
         String annotation = parseAnnotations(arg3);
         byte[] bytes = dateToString(date).getBytes();
         byte[] bytes1 = SPLITTER.getBytes();
         byte[] bytes2 = annotation.getBytes();

         System.out.println("*** bytes to write " + (bytes.length + bytes1.length + bytes2.length));
         stream.write(bytes);
         stream.write(bytes1);
         stream.write(bytes2);
      }

      @Override
      public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                MediaType arg3) {
         return isWriteable(arg0, arg1, arg2, arg3);
      }

      @Override
      public Date readFrom(Class<Date> arg0, Type arg1, Annotation[] arg2,
                           MediaType arg3, MultivaluedMap<String, String> arg4,
                           InputStream arg5) throws IOException, WebApplicationException {
         InputStreamReader reader = new InputStreamReader(arg5);
         BufferedReader br = new BufferedReader(reader);
         long date = Long.parseLong(br.readLine());
         return new Date(date);
      }

      protected String parseAnnotations(Annotation[] annotations) {
         StringBuilder value = new StringBuilder();
         if (annotations != null)
            for (Annotation annotation : annotations)
               value.append(annotation.annotationType().getName())
                       .append(", ");
         return value.toString();
      }

      public static final String dateToString(Date date) {
         return String.valueOf(date.getTime());
      }
   }

   @Provider
   public static class DateClientReaderWriter implements MessageBodyReader<Date>,
           MessageBodyWriter<Date> {
      private StringBuilder atom;

      public DateClientReaderWriter(StringBuilder atom) {
         super();
         this.atom = atom;
      }

      @Override
      public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4) {
         return String.valueOf(Long.MAX_VALUE).length()
                 + DateContainerReaderWriter.SPLITTER.length();
      }

      @Override
      public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                 MediaType arg3) {
         return arg0 == Date.class;
      }

      @Override
      public void writeTo(Date date, Class<?> arg1, Type arg2, Annotation[] arg3,
                          MediaType arg4, MultivaluedMap<String, Object> arg5,
                          OutputStream stream) throws IOException, WebApplicationException {
         byte[] bytes = dateToString(date).getBytes();
         stream.write(bytes);
      }

      @Override
      public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
                                MediaType arg3) {
         return isWriteable(arg0, arg1, arg2, arg3);
      }

      @Override
      public Date readFrom(Class<Date> arg0, Type arg1, Annotation[] arg2,
                           MediaType arg3, MultivaluedMap<String, String> arg4,
                           InputStream arg5) throws IOException, WebApplicationException {
         InputStreamReader reader = new InputStreamReader(arg5);
         BufferedReader br = new BufferedReader(reader);
         String data = br.readLine();
         String[] split = data == null ? new String[] { "0" } : data
                 .split(DateContainerReaderWriter.SPLITTER);
         long date = Long.parseLong(split[0]);
         atom.append(split[1]);
         return new Date(date);
      }

      public static final String dateToString(Date date) {
         return String.valueOf(date.getTime());
      }
   }



   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(DateContainerReaderWriter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      client.close();
   }

   @Test
   public void testGetSizeIgnored()
   {
      Date date = Calendar.getInstance().getTime();
      String entity = DateContainerReaderWriter.dateToString(date);
      StringBuilder sb = new StringBuilder();
      DateClientReaderWriter rw = new DateClientReaderWriter(sb);


      Response response = client.target(generateURL("/entity")).register(rw).request().post(Entity.text(entity));

      Date responseDate = response.readEntity(Date.class);
      Assert.assertTrue(date.equals(responseDate));

      Annotation[] annotations = AnnotatedClass.class.getAnnotations();
      for (Annotation annotation : annotations) {
         String name = annotation.annotationType().getName();
         Assert.assertTrue(sb.toString().contains(name));
      }
   }



}
