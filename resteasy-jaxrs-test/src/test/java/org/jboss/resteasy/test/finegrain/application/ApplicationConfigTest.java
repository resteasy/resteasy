package org.jboss.resteasy.test.finegrain.application;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationConfigTest
{
   @Path("/myinterface")
   public static interface MyInterface
   {
      @GET
      @Produces("text/plain")
      public String hello();

   }

   public static class MyService implements MyInterface
   {
      public String hello()
      {
         return "hello";
      }
   }

   @Path("/my")
   public static class MyResource
   {
      @GET
      @Produces("text/quoted")
      public String get()
      {
         return "hello";
      }
   }

   @Path("/injection")
   @Produces("text/plain")
   public static class InjectionResource
   {
      private MyApplicationConfig application;

      @Path("/field")
      @GET
      public boolean fieldInjection()
      {
         return getApplication().isFieldInjected();
      }

      @Path("/setter")
      @GET
      public boolean setterInjection()
      {
         return getApplication().isSetterInjected();
      }

      @Path("/constructor")
      @GET
      public boolean constructorInjection()
      {
         return getApplication().isConstructorInjected();
      }

      private MyApplicationConfig getApplication()
      {
         return application;
      }

      @Context
      public void setApplication(Application app)
      {
         this.application = (MyApplicationConfig) app;
      }
   }

   @Provider
   @Produces("text/quoted")
   public static class QuotedTextWriter implements MessageBodyWriter<String>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(String.class);
      }

      public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
              WebApplicationException
      {
         s = "\"" + s + "\"";
         entityStream.write(s.getBytes());
      }
   }


   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplicationClass("org.jboss.resteasy.test.finegrain.application.MyApplicationConfig");
      EmbeddedContainer.start(deployment);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(String uri, String body)
   {
      {
         ClientRequest request = new ClientRequest(uri);
         try
         {
            ClientResponse<String> response = request.get(String.class);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         } catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testIt()
   {
      _test(generateURL("/my"), "\"hello\"");
      _test(generateURL("/myinterface"), "hello");
   }

   @Test
   public void testFieldInjection()
   {
      _test(generateURL("/injection/field"), "true");
   }

   @Test
   public void testSetterInjection()
   {
      _test(generateURL("/injection/setter"), "true");
   }

   @Test
   public void testConstructorInjection()
   {
      _test(generateURL("/injection/constructor"), "true");
   }
}
