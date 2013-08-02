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
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MapProviderTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource {
      @Path("map")
      @POST
      public MultivaluedMap<String, String> map(MultivaluedMap<String, String> map) {
         return map;
      }
   }

   public static abstract class AbstractProvider {
      public long getLength() {
         String name = getClass().getSimpleName().replace("Provider", "");
         long size = "writer".length() + name.length();
         return 2*size;
      }

      public String getWriterName() {
         String name = getClass().getSimpleName().replace("Provider", "Writer");
         return name;
      }

      public String getReaderName() {
         String name = getClass().getSimpleName().replace("Provider", "Reader");
         return name;
      }

   }


   @Provider
   @Produces(MediaType.APPLICATION_FORM_URLENCODED)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   public static class MapProvider extends AbstractProvider implements
                                                        MessageBodyReader<MultivaluedMap<String, String>>,
                                                        MessageBodyWriter<MultivaluedMap<String, String>> {

      @Override
      public long getSize(MultivaluedMap<String, String> t, Class<?> type,
                          Type genericType, Annotation[] annotations, MediaType mediaType) {
         return getLength();
      }

      @Override
      public boolean isWriteable(Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType) {
         return MultivaluedMap.class.isAssignableFrom(type);
      }

      @Override
      public void writeTo(MultivaluedMap<String, String> t, Class<?> type,
                          Type genericType, Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders,
                          OutputStream entityStream) throws IOException,
              WebApplicationException {
         entityStream.write(t.getFirst(getClass().getSimpleName()).getBytes());
         entityStream.write(getWriterName().getBytes());
      }

      @Override
      public boolean isReadable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType) {
         return isWriteable(type, genericType, annotations, mediaType);
      }

      @Override
      public MultivaluedMap<String, String> readFrom(
              Class<MultivaluedMap<String, String>> type, Type genericType,
              Annotation[] annotations, MediaType mediaType,
              MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
              throws IOException, WebApplicationException {
         MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
         map.add(getClass().getSimpleName(), getReaderName());
         return map;
      }

   }


   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(MapProvider.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testMap()
   {
      // writers sorted by type, mediatype, and then by app over builtin
      Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.entity("map", MediaType.APPLICATION_FORM_URLENCODED_TYPE));
      Assert.assertEquals(response.getStatus(), 200);
      String data = response.readEntity(String.class);
      System.out.println(data);
      Assert.assertTrue(data.contains("MapWriter"));
      response.close();
   }

}
