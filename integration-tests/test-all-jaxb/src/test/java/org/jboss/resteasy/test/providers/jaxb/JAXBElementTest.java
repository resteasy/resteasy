package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JAXBElementTest extends BaseResourceTest
{
   public static class ReadableWritableEntity {
      private String entity;
      public static final String NAME = "READABLEWRITEABLE";
      private static final String PREFIX = "<" + NAME + ">";
      private static final String SUFFIX = "</" + NAME + ">";

      public ReadableWritableEntity(String entity) {
         this.entity = entity;
      }

      public String toXmlString() {
         StringBuilder sb = new StringBuilder();
         sb.append(PREFIX).append(entity).append(SUFFIX);
         return sb.toString();
      }

      @Override
      public String toString() {
         return entity;
      }

      public static ReadableWritableEntity fromString(String stream) {
         String entity = stream.replaceAll(PREFIX, "").replaceAll(SUFFIX, "");
         return new ReadableWritableEntity(entity);
      }
   }

   public static class EntityMessageReader implements
           MessageBodyReader<ReadableWritableEntity>
   {

      @Override
      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType) {
         return ReadableWritableEntity.class.isAssignableFrom(type);
      }

      @Override
      public ReadableWritableEntity readFrom(Class<ReadableWritableEntity> arg0,
                                             Type arg1, Annotation[] annotations, MediaType mediaType,
                                             MultivaluedMap<String, String> arg4, InputStream entityStream)
              throws IOException, WebApplicationException
      {
         String entity = readInputStream(entityStream);
         return ReadableWritableEntity.fromString(entity);
      }

      String readInputStream(InputStream is) throws IOException {
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         return br.readLine();
      }

   }

   public static class EntityMessageWriter implements
           MessageBodyWriter<ReadableWritableEntity>
   {

      @Override
      public long getSize(ReadableWritableEntity t, Class<?> type,
                          Type genericType, Annotation[] annotations, MediaType mediaType) {
         return t.toXmlString().length();
      }

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return ReadableWritableEntity.class.isAssignableFrom(type);
      }

      @Override
      public void writeTo(ReadableWritableEntity t, Class<?> type,
                          Type genericType, Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         entityStream.write(t.toXmlString().getBytes());
      }

   }


   @Path("resource")
   public static class Resource {

      @GET
      @Path("readerprovider")
      public ReadableWritableEntity clientReader() {
         return new ReadableWritableEntity(getClass().getName());
      }

      @POST
      @Path("writerprovider")
      public String clientWriter(ReadableWritableEntity entity) {
         return entity.toXmlString();
      }


      @GET
      @Path("standardreader")
      public String bytearrayreader(@Context HttpHeaders headers){
         String name = Resource.class.getName();
         MediaType type = headers.getAcceptableMediaTypes().iterator().next();
         if (type != null && type.getSubtype().contains("xml"))
            name = "<resource>" + name + "</resource>";
         return name;
      }

      @POST
      @Path("standardwriter")
      public String bytearraywriter(String value){
         return value;
      }
   }


   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(EntityMessageReader.class);
      deployment.getProviderFactory().register(EntityMessageWriter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testWriter()
   {
      JAXBElement<String> element = new JAXBElement<String>(new QName(""),
              String.class, Resource.class.getName());
      Response response = client.target(generateURL("/resource/standardwriter")).request().post(Entity.xml(element));
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }



}
