package org.jboss.resteasy.test.providers.jaxb.regression;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-510
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomOverrideTest extends BaseResourceTest
{
   @Provider
   @Produces("text/x-vcard")
   public static class VCardMessageBodyWriter implements MessageBodyWriter<Foo>
   {
      @Override
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      @Override
      public long getSize(Foo foo, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      @Override
      public void writeTo(Foo foo, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         String msg = "---" + foo.getName() + "---";
         entityStream.write(msg.getBytes());
      }
   }

   @Path("/test")
   @Produces("application/xml")
   public static class VCardResource
   {
      @GET
      public Response getFooXml()
      {
         Foo foo = new Foo();
         foo.setName("bill");
         return Response.ok(foo).build();
      }

      @GET
      @Produces("text/x-vcard")
      public Response getFooVcard()
      {
         Foo foo = new Foo();
         foo.setName("bill");
         return Response.ok(foo).build();
      }
   }

   @Override
   @Before
   public void before() throws Exception
   {
      registerProvider(VCardMessageBodyWriter.class);
      addPerRequestResource(VCardResource.class, Foo.class);
      super.before();
   }

   @Test
   public void testRegression() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test"));
      request.accept("text/x-vcard");
      String response = request.getTarget(String.class);
      System.out.println(response);
      request.clear();
      request.accept("application/xml");
      response = request.getTarget(String.class);
      System.out.println(response);


   }

}
