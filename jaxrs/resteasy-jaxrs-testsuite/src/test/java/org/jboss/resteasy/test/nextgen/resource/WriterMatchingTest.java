package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterMatchingTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource {
      @GET
      @Path("bool")
      public Boolean responseOk() {
         return true;
      }
   }

   @Produces(MediaType.WILDCARD)
   public static class BoolWriter implements MessageBodyWriter<Object>
   {
      @Override
      public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Boolean.class);
      }

      @Override
      public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      @Override
      public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         Boolean b = (Boolean)o;
         if (b.booleanValue()) entityStream.write("YES".getBytes());
         else entityStream.write("NO".getBytes());
      }
   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(BoolWriter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testMatch()
   {
      // writers sorted by type, mediatype, and then by app over builtin
      Response response = client.target(generateURL("/bool")).request("text/plain").get();
      Assert.assertEquals(response.getStatus(), 200);
      String data = response.readEntity(String.class);
      System.out.println(data);
      response.close();
      Assert.assertEquals(data, "true");
   }

}
