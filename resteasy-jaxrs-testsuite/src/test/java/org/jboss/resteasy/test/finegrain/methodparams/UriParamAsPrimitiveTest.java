package org.jboss.resteasy.test.finegrain.methodparams;


import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.jboss.resteasy.util.HttpClient4xUtils.updateQuery;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriParamAsPrimitiveTest
{
   private static ResteasyDeployment deployment;

   private static IResourceUriBoolean resourceUriBoolean;

   private static IResourceUriByte resourceUriByte;

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(ResourceUriBoolean.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriByte.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriShort.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriInt.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriLong.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriFloat.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriDouble.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriBooleanWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriByteWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriShortWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriIntWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriLongWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriFloatWrapper.class);
      deployment.getRegistry().addPerRequestResource(ResourceUriDoubleWrapper.class);
      resourceUriBoolean = ProxyFactory.create(IResourceUriBoolean.class, generateBaseUrl());
      resourceUriByte = ProxyFactory.create(IResourceUriByte.class, generateBaseUrl());
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/boolean/{arg}")
   public static class ResourceUriBoolean
   {
      @GET
      public String doGet(@PathParam("arg") boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }
   }

   @Path("/boolean/{arg}")
   public interface IResourceUriBoolean
   {
      @GET
      String doGet(@PathParam("arg") boolean v);
   }

   @Path("/byte/{arg}")
   public static class ResourceUriByte
   {
      @GET
      public String doGet(@PathParam("arg") byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }
   }

   @Path("/byte/{arg}")
   public interface IResourceUriByte
   {
      @GET
      String doGet(@PathParam("arg") byte v);
   }

   @Path("/short/{arg}")
   public static class ResourceUriShort
   {
      @GET
      public String doGet(@PathParam("arg") short v)
      {
         Assert.assertTrue(32767 == v);
         return "content";
      }
   }

   @Path("/int/{arg}")
   public static class ResourceUriInt
   {
      @GET
      public String doGet(@PathParam("arg") int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }
   }

   @Path("/long/{arg}")
   public static class ResourceUriLong
   {
      @GET
      public String doGet(@PathParam("arg") long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }
   }

   @Path("/float/{arg}")
   public static class ResourceUriFloat
   {
      @GET
      public String doGet(@PathParam("arg") float v)
      {
         Assert.assertEquals(3.14159265f, v, 0.0000000001f);
         return "content";
      }
   }

   @Path("/double/{arg}")
   public static class ResourceUriDouble
   {
      @GET
      public String doGet(@PathParam("arg") double v)
      {
         Assert.assertEquals(3.14159265358979d, v, 0.0000000000000001d);
         return "content";
      }
   }

   @Path("/boolean/wrapper/{arg}")
   public static class ResourceUriBooleanWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }
   }

   @Path("/byte/wrapper/{arg}")
   public static class ResourceUriByteWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());
         return "content";
      }
   }

   @Path("/short/wrapper/{arg}")
   public static class ResourceUriShortWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }
   }

   @Path("/int/wrapper/{arg}")
   public static class ResourceUriIntWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }
   }

   @Path("/long/wrapper/{arg}")
   public static class ResourceUriLongWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }
   }

   @Path("/float/wrapper/{arg}")
   public static class ResourceUriFloatWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue(), 0.0000000001f);
         return "content";
      }
   }

   @Path("/double/wrapper/{arg}")
   public static class ResourceUriDoubleWrapper
   {
      @GET
      public String doGet(@PathParam("arg") Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue(), 0.0000000000000001d);
         return "content";
      }
   }

   void _test(String type, String value)
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/" + type + "/" + value));
         try
         {
            ClientResponse<?> response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.releaseConnection();
         } catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      
      {
         ClientRequest request = new ClientRequest(generateURL("/" + type + "/wrapper/" + value));
         try
         {
            ClientResponse<?> response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.releaseConnection();
         } catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testGetBoolean()
   {
      _test("boolean", "true");
      resourceUriBoolean.doGet(true);
   }

   @Test
   public void testGetByte()
   {
      _test("byte", "127");
      resourceUriByte.doGet((byte) 127);
   }

   @Test
   public void testGetShort()
   {
      _test("short", "32767");
   }

   @Test
   public void testGetInt()
   {
      _test("int", "2147483647");
   }

   @Test
   public void testGetLong()
   {
      _test("long", "9223372036854775807");
   }

   @Test
   public void testGetFloat()
   {
      _test("float", "3.14159265");
   }

   @Test
   public void testGetDouble()
   {
      _test("double", "3.14159265358979");
   }

   public void testBadPrimitiveValue()
   {
      String uri = updateQuery(generateURL("/int/abcdef"), "int=abcdef");
      ClientRequest request = new ClientRequest(uri);
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testBadPrimitiveWrapperValue()
   {
      String uri = updateQuery(generateURL("/int/wrapper/abcdef"), "int=abcdef");
      ClientRequest request = new ClientRequest(uri);
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.releaseConnection();
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
