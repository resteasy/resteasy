package org.jboss.resteasy.test.nextgen.producers;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.DefaultNumberWriter;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1282
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 23, 2016
 */
public class TestDefaultNumberWriter
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("")
   public static class TestResource
   {
      @Path("test/{type}")
      @GET
      public Response get(@PathParam("type") String type) throws Exception
      {
         if ("Byte".equals(type))
         {
            return Response.ok().entity(new Byte((byte) 123)).build();     
         }
         else if ("byte".equals(type))
         {
            return Response.ok().entity((byte) 123).build();     
         }
         else if ("Double".equals(type))
         {
            return Response.ok().entity(new Double((double) 123.4)).build();     
         }
         else if ("double".equals(type))
         {
            return Response.ok().entity((double) 123.4).build();     
         }
         else if ("Float".equals(type))
         {
            return Response.ok().entity(new Float((float) 123.4)).build();     
         }
         else if ("float".equals(type))
         {
            return Response.ok().entity((float) 123.4).build();     
         }
         else if ("Integer".equals(type))
         {
            return Response.ok().entity(new Integer((int) 123)).build();     
         }
         else if ("integer".equals(type))
         {
            return Response.ok().entity((int) 123).build();
         }
         else if ("Long".equals(type))
         {
            return Response.ok().entity(new Long((long) 123)).build();     
         }
         else if ("long".equals(type))
         {
            return Response.ok().entity((long) 123).build();     
         }
         else if ("Short".equals(type))
         {
            return Response.ok().entity(new Short((short) 123)).build();     
         }
         else if ("short".equals(type))
         {
            return Response.ok().entity((short) 123).build();
         }
         else if ("bigDecimal".equals(type))
         {
            return Response.ok().entity(new BigDecimal(123)).build();
         }
         else
         {
            throw new RuntimeException("unexpected type: " + type);
         }
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().registerProvider(MyDefaultNumberWriter.class);
      MyDefaultNumberWriter.used = false;
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testByte() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Byte")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testBytePrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/byte")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testDouble() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Double")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123.4", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testDoublePrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/double")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123.4", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testFloat() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Float")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123.4", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testFloatPrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/float")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123.4", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testInteger() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Integer")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testIntegerPrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/integer")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testLong() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Long")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testLongPrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/long")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testShort() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/Short")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testShortPrimitive() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/short")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testBigDecimal() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/bigDecimal")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Test
   public void testProviderGetsUsed() throws Exception
   {
      Client client = ClientBuilder.newClient();
      client.register(MyDefaultNumberWriter.class);
      Response response = client.target(generateURL("/test/bigDecimal")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123", response.getEntity());
      Assert.assertTrue(MyDefaultNumberWriter.used);
   }
   
   @Provider
   public static class MyDefaultNumberWriter extends DefaultNumberWriter
   {
      static boolean used;
      
      @Override
      public void writeTo(Number n, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
         System.out.println("MyDefaultNumberWriter.writeTo()");
         used = true;
         super.writeTo(n, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      }
   }
}
