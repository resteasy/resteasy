package org.jboss.resteasy.test.nextgen.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1250
 * 
 * @author Shane D
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class GenericReturnTypeTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   public interface TestInterface<T>
   {
      @GET
      @Path("t")
      T t();
   }

   @Path("")
   public static class TestResource implements TestInterface<String>
   {
      @GET
      @Path("t")
      @Produces("text/plain")
      public String t()
      {
         return "abc";
      }
   }
   
   public static class ObjectReader implements MessageBodyReader<Object>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      @Override
      public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         StringBuffer sb = new StringBuffer();
         int c = entityStream.read();
         while (c != -1)
         {
            sb.append((char)c);
            c = entityStream.read();
         }
         return sb.toString();
      }
      
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testGenericReturnType()
   {
      Client client = ResteasyClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081").register(ObjectReader.class);
      TestInterface<?> server = ProxyBuilder.builder(TestInterface.class, target).build();
      Object result = server.t();
      System.out.println(result);
      Assert.assertEquals("abc", result);
   }
}
