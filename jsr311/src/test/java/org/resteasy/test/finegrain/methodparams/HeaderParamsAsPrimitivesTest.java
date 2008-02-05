package org.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamsAsPrimitivesTest
{
   private HttpClient client = new HttpClient();

   private static HttpServletDispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitives.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitivesDefault.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitivesDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveWrappers.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveList.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveListDefault.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceHeaderPrimitiveListDefaultOverride.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Path("/")
   public static class ResourceHeaderPrimitives
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte")byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short")short v)
      {
         Assert.assertTrue(v == 32767);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/null")
   public static class ResourceHeaderPrimitivesDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean")boolean v)
      {
         Assert.assertEquals(false, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte")byte v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short")short v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int")int v)
      {
         Assert.assertEquals(0, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long")long v)
      {
         Assert.assertEquals(0l, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float")float v)
      {
         Assert.assertEquals(0.0f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double")double v)
      {
         Assert.assertEquals(0.0d, v);
         return "content";
      }
   }

   @Path("/default")
   public static class ResourceHeaderPrimitivesDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("true")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("127")byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("32767")short v)
      {
         Assert.assertTrue(32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("2147483647")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("9223372036854775807")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("3.14159265")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("3.14159265358979")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/override")
   public static class ResourceHeaderPrimitivesDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("false")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("1")byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("1")short v)
      {
         Assert.assertTrue(32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("1")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("1")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("0.0")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("0.0")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/wrappers")
   public static class ResourceHeaderPrimitiveWrappers
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte")Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());

         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short")Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/null")
   public static class ResourceHeaderPrimitiveWrappersDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean")Boolean v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte")Byte v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short")Short v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int")Integer v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long")Long v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float")Float v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double")Double v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/wrappers/default")
   public static class ResourceHeaderPrimitiveWrappersDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("true")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("127")Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());

         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("32767")Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("2147483647")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("9223372036854775807")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("3.14159265")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("3.14159265358979")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/override")
   public static class ResourceHeaderPrimitiveWrappersDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("false")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("1")Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("1")Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("1")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("1")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("0.0")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("0.0")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/list")
   public static class ResourceHeaderPrimitiveList
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         Assert.assertEquals(true, v.get(1).booleanValue());
         Assert.assertEquals(true, v.get(2).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@HeaderParam("byte")List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         Assert.assertTrue(127 == v.get(1).byteValue());
         Assert.assertTrue(127 == v.get(2).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@HeaderParam("short")List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         Assert.assertTrue(32767 == v.get(1).shortValue());
         Assert.assertTrue(32767 == v.get(2).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@HeaderParam("int")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         Assert.assertEquals(2147483647, v.get(1).intValue());
         Assert.assertEquals(2147483647, v.get(2).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@HeaderParam("long")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(1).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(2).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@HeaderParam("float")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         Assert.assertEquals(3.14159265f, v.get(1).floatValue());
         Assert.assertEquals(3.14159265f, v.get(2).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@HeaderParam("double")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(1).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(2).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/null")
   public static class ResourceHeaderPrimitiveListDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@HeaderParam("byte")List<Byte> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@HeaderParam("short")List<Short> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@HeaderParam("int")List<Integer> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@HeaderParam("long")List<Long> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@HeaderParam("float")List<Float> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@HeaderParam("double")List<Double> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/list/default")
   public static class ResourceHeaderPrimitiveListDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("true")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@HeaderParam("byte") @DefaultValue("127")List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@HeaderParam("short") @DefaultValue("32767")List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@HeaderParam("int") @DefaultValue("2147483647")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@HeaderParam("long") @DefaultValue("9223372036854775807")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@HeaderParam("float") @DefaultValue("3.14159265")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@HeaderParam("double") @DefaultValue("3.14159265358979")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/override")
   public static class ResourceHeaderPrimitiveListDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@HeaderParam("byte") @DefaultValue("0")List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@HeaderParam("short") @DefaultValue("0")List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@HeaderParam("int") @DefaultValue("0")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@HeaderParam("long") @DefaultValue("0")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@HeaderParam("float") @DefaultValue("0.0")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@HeaderParam("double") @DefaultValue("0.0")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }


   public void _test(String type, String value)
   {
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
         request.setPathInfo("/");
         request.addHeader(type, value);
         request.addHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         method.addRequestHeader(type, value);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         GetMethod method = new GetMethod("http://localhost:8081/wrappers");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         method.addRequestHeader(type, value);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpServletResponse.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         GetMethod method = new GetMethod("http://localhost:8081/list");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         method.addRequestHeader(type, value);
         method.addRequestHeader(type, value);
         method.addRequestHeader(type, value);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpServletResponse.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public void _testDefault(String base, String type, String value)
   {
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", base + "default/null");
         request.setPathInfo(base + "default/null");
         request.addHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081" + base + "default/null");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", base + "default");
         request.setPathInfo(base + "default");
         request.addHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081" + base + "default");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081" + base + "default/override");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/" + type);
         method.addRequestHeader(type, value);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public void _testDefault(String type, String value)
   {
      _testDefault("/", type, value);
   }

   public void _testWrappersDefault(String type, String value)
   {
      _testDefault("/wrappers/", type, value);
   }

   public void _testListDefault(String type, String value)
   {
      _testDefault("/list/", type, value);
   }

   @Test
   public void testGetBoolean()
   {
      _test("boolean", "true");
   }

   @Test
   public void testGetBooleanPrimitivesDefault()
   {
      _testDefault("boolean", "true");
   }

   @Test
   public void testGetBooleanPrimitiveWrapperDefault()
   {
      _testWrappersDefault("boolean", "true");
   }

   @Test
   public void testGetBooleanPrimitiveListDefault()
   {
      _testListDefault("boolean", "true");
   }

   @Test
   public void testGetByte()
   {
      _test("byte", "127");
   }

   @Test
   public void testGetBytePrimitivesDefault()
   {
      _testDefault("byte", "127");
   }

   @Test
   public void testGetBytePrimitiveWrappersDefault()
   {
      _testWrappersDefault("byte", "127");
   }

   @Test
   public void testGetBytePrimitiveListDefault()
   {
      _testListDefault("byte", "127");
   }

   @Test
   public void testGetShort()
   {
      _test("short", "32767");
   }

   @Test
   public void testGetShortPrimtivesDefault()
   {
      _testDefault("short", "32767");
   }

   @Test
   public void testGetShortPrimtiveWrappersDefault()
   {
      _testWrappersDefault("short", "32767");
   }

   @Test
   public void testGetShortPrimtiveListDefault()
   {
      _testListDefault("short", "32767");
   }

   @Test
   public void testGetInt()
   {
      _test("int", "2147483647");
   }

   @Test
   public void testGetIntPrimitivesDefault()
   {
      _testDefault("int", "2147483647");
   }

   @Test
   public void testGetIntPrimitiveWrappersDefault()
   {
      _testWrappersDefault("int", "2147483647");
   }

   @Test
   public void testGetIntPrimitiveListDefault()
   {
      _testListDefault("int", "2147483647");
   }

   @Test
   public void testGetLong()
   {
      _test("long", "9223372036854775807");
   }

   @Test
   public void testGetLongPrimitivesDefault()
   {
      _testDefault("long", "9223372036854775807");
   }

   @Test
   public void testGetLongPrimitiveWrappersDefault()
   {
      _testWrappersDefault("long", "9223372036854775807");
   }

   @Test
   public void testGetLongPrimitiveListDefault()
   {
      _testListDefault("long", "9223372036854775807");
   }

   @Test
   public void testGetFloat()
   {
      _test("float", "3.14159265");
   }

   @Test
   public void testGetFloatPrimitivesDefault()
   {
      _testDefault("float", "3.14159265");
   }

   @Test
   public void testGetFloatPrimitiveWrappersDefault()
   {
      _testWrappersDefault("float", "3.14159265");
   }

   @Test
   public void testGetFloatPrimitiveListDefault()
   {
      _testListDefault("float", "3.14159265");
   }

   @Test
   public void testGetDouble()
   {
      _test("double", "3.14159265358979");
   }

   @Test
   public void testGetDoublePrimitivesDefault()
   {
      _testDefault("double", "3.14159265358979");
   }

   @Test
   public void testGetDoublePrimitiveWrappersDefault()
   {
      _testWrappersDefault("double", "3.14159265358979");
   }

   @Test
   public void testGetDoublePrimitiveListDefault()
   {
      _testListDefault("double", "3.14159265358979");
   }

   @Test
   public void testBadPrimitiveValue()
   {
      {
         GetMethod method = new GetMethod("http://localhost:8081/");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
         method.addRequestHeader("int", "abcdef");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, 400);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testBadPrimitiveWrapperValue()
   {
      {
         GetMethod method = new GetMethod("http://localhost:8081/wrappers");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
         method.addRequestHeader("int", "abcdef");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, 400);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testBadPrimitiveListValue()
   {
      {
         GetMethod method = new GetMethod("http://localhost:8081/list");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
         method.addRequestHeader("int", "abcdef");
         method.addRequestHeader("int", "abcdef");
         method.addRequestHeader("int", "abcdef");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, 400);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}
