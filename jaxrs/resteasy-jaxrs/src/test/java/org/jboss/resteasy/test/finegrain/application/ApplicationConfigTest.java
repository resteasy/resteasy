package org.jboss.resteasy.test.finegrain.application;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.TJWSServletContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationConfigTest
{
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

   public static class MyApplicationConfig extends Application
   {
      private Set<Class<?>> classes = new HashSet<Class<?>>();

      public MyApplicationConfig()
      {
         classes.add(MyResource.class);
         classes.add(QuotedTextWriter.class);
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return classes;
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      TJWSServletContainer.start();
      TJWSServletContainer.tjws.addApplicationConfig(new MyApplicationConfig());
   }

   @AfterClass
   public static void after() throws Exception
   {
      TJWSServletContainer.stop();
   }

   private void _test(HttpClient client, String uri, String body)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Assert.assertEquals(body, method.getResponseBodyAsString());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

   @Test
   public void testIt()
   {
      HttpClient client = new HttpClient();
      _test(client, generateURL("/my"), "\"hello\"");
   }
}
