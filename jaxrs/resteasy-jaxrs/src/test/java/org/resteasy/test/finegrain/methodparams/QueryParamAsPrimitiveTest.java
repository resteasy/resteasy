package org.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.spi.Dispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamAsPrimitiveTest
{

   private static HttpClient client = new HttpClient();
   private static Dispatcher dispatcher;

   private static IResourceQueryPrimitives resourceQueryPrimitives;
   private static IResourceQueryPrimitiveWrappers resourceQueryPrimitiveWrappers;
   private static IResourceQueryPrimitiveList resourceQueryPrimitiveList;
   private static IResourceQueryPrimitiveArray resourceQueryPrimitiveArray;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(ResourceQueryPrimitives.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitivesDefault.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitivesDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveWrappers.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveList.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveListDefault.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveListDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveArray.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveArrayDefault.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveArrayDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceQueryPrimitiveArrayDefaultOverride.class);
      resourceQueryPrimitives = ProxyFactory.create(IResourceQueryPrimitives.class, "http://localhost:8081");
      resourceQueryPrimitiveWrappers = ProxyFactory.create(IResourceQueryPrimitiveWrappers.class, "http://localhost:8081");
      resourceQueryPrimitiveList = ProxyFactory.create(IResourceQueryPrimitiveList.class, "http://localhost:8081");
      resourceQueryPrimitiveArray = ProxyFactory.create(IResourceQueryPrimitiveArray.class, "http://localhost:8081");
   }

   @AfterClass
   public static void after() throws Exception
   {
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitives.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitivesDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitivesDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveWrappers.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveList.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveListDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveListDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveArray.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveArrayDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveArrayDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceQueryPrimitiveArrayDefaultOverride.class);
      EmbeddedServletContainer.stop();
   }

   @Path("/")
   public static class ResourceQueryPrimitives implements IResourceQueryPrimitives
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/null")
   public static class ResourceQueryPrimitivesDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean")boolean v)
      {
         Assert.assertEquals(false, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte")byte v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short")short v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int")int v)
      {
         Assert.assertEquals(0, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long")long v)
      {
         Assert.assertEquals(0l, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float")float v)
      {
         Assert.assertEquals(0.0f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double")double v)
      {
         Assert.assertEquals(0.0d, v);
         return "content";
      }
   }

   @Path("/default")
   public static class ResourceQueryPrimitivesDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean") @DefaultValue("true")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte") @DefaultValue("127")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short") @DefaultValue("32767")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int") @DefaultValue("2147483647")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long") @DefaultValue("9223372036854775807")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float") @DefaultValue("3.14159265")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double") @DefaultValue("3.14159265358979")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/override")
   public static class ResourceQueryPrimitivesDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean") @DefaultValue("false")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte") @DefaultValue("1")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short") @DefaultValue("1")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int") @DefaultValue("1")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long") @DefaultValue("1")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float") @DefaultValue("0.0")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double") @DefaultValue("0.0")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/wrappers")
   public static class ResourceQueryPrimitiveWrappers implements IResourceQueryPrimitiveWrappers
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/null")
   public static class ResourceQueryPrimitiveWrappersDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean")Boolean v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte")Byte v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short")Short v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int")Integer v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long")Long v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float")Float v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double")Double v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/wrappers/default")
   public static class ResourceQueryPrimitiveWrappersDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean") @DefaultValue("true")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte") @DefaultValue("127")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short") @DefaultValue("32767")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int") @DefaultValue("2147483647")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long") @DefaultValue("9223372036854775807")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float") @DefaultValue("3.14159265")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double") @DefaultValue("3.14159265358979")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/override")
   public static class ResourceQueryPrimitiveWrappersDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@QueryParam("boolean") @DefaultValue("false")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@QueryParam("byte") @DefaultValue("1")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@QueryParam("short") @DefaultValue("1")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@QueryParam("int") @DefaultValue("1")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@QueryParam("long") @DefaultValue("1")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@QueryParam("float") @DefaultValue("0.0")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@QueryParam("double") @DefaultValue("0.0")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/list")
   public static class ResourceQueryPrimitiveList implements IResourceQueryPrimitiveList
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         Assert.assertEquals(true, v.get(1).booleanValue());
         Assert.assertEquals(true, v.get(2).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         Assert.assertTrue((byte) 127 == v.get(1).byteValue());
         Assert.assertTrue((byte) 127 == v.get(2).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         Assert.assertTrue((short) 32767 == v.get(1).shortValue());
         Assert.assertTrue((short) 32767 == v.get(2).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         Assert.assertEquals(2147483647, v.get(1).intValue());
         Assert.assertEquals(2147483647, v.get(2).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(1).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(2).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         Assert.assertEquals(3.14159265f, v.get(1).floatValue());
         Assert.assertEquals(3.14159265f, v.get(2).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(1).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(2).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/null")
   public static class ResourceQueryPrimitiveListDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte")List<Byte> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short")List<Short> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int")List<Integer> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long")List<Long> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float")List<Float> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double")List<Double> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/list/default")
   public static class ResourceQueryPrimitiveListDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean") @DefaultValue("true")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte") @DefaultValue("127")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short") @DefaultValue("32767")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int") @DefaultValue("2147483647")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long") @DefaultValue("9223372036854775807")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float") @DefaultValue("3.14159265")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double") @DefaultValue("3.14159265358979")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/override")
   public static class ResourceQueryPrimitiveListDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean") @DefaultValue("false")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte") @DefaultValue("0")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short") @DefaultValue("0")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int") @DefaultValue("0")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long") @DefaultValue("0")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float") @DefaultValue("0.0")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double") @DefaultValue("0.0")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/array")
   public static class ResourceQueryPrimitiveArray implements IResourceQueryPrimitiveArray
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         Assert.assertEquals(true, v[1]);
         Assert.assertEquals(true, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         Assert.assertTrue((byte) 127 == v[1]);
         Assert.assertTrue((byte) 127 == v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short")short[] v)
      {
         Assert.assertTrue((short) 32767 == v[0]);
         Assert.assertTrue((short) 32767 == v[1]);
         Assert.assertTrue((short) 32767 == v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         Assert.assertEquals(2147483647, v[1]);
         Assert.assertEquals(2147483647, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         Assert.assertEquals(9223372036854775807L, v[1]);
         Assert.assertEquals(9223372036854775807L, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         Assert.assertEquals(3.14159265f, v[1]);
         Assert.assertEquals(3.14159265f, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         Assert.assertEquals(3.14159265358979d, v[1]);
         Assert.assertEquals(3.14159265358979d, v[2]);
         return "content";
      }
   }

   @Path("/array/default/null")
   public static class ResourceQueryPrimitiveArrayDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean")boolean[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte")byte[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short")short[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int")int[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long")long[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float")float[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double")double[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/array/default")
   public static class ResourceQueryPrimitiveArrayDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean") @DefaultValue("true")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte") @DefaultValue("127")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short") @DefaultValue("32767")short[] v)
      {
         Assert.assertTrue((short) 32767 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int") @DefaultValue("2147483647")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long") @DefaultValue("9223372036854775807")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float") @DefaultValue("3.14159265")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double") @DefaultValue("3.14159265358979")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         return "content";
      }
   }

   @Path("/array/default/override")
   public static class ResourceQueryPrimitiveArrayDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@QueryParam("boolean") @DefaultValue("false")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@QueryParam("byte") @DefaultValue("0")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@QueryParam("short") @DefaultValue("0")short[] v)
      {
         Assert.assertTrue((short) 32767 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@QueryParam("int") @DefaultValue("0")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@QueryParam("long") @DefaultValue("0")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@QueryParam("float") @DefaultValue("0.0")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@QueryParam("double") @DefaultValue("0.0")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         return "content";
      }
   }

   public void _test(String type, String value)
   {
      String param = type + "=" + value;

      {
         GetMethod method = new GetMethod("http://localhost:8081/");
         method.setQueryString(param);
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
         GetMethod method = new GetMethod("http://localhost:8081/wrappers");
         method.setQueryString(param);
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
         GetMethod method = new GetMethod("http://localhost:8081/list");
         method.setQueryString(param + "&" + param + "&" + param);
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
         GetMethod method = new GetMethod("http://localhost:8081/array");
         method.setQueryString(param + "&" + param + "&" + param);
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
   }

   public void _testDefault(String base, String type, String value)
   {
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
      String param = type + "=" + value;
      {
         GetMethod method = new GetMethod("http://localhost:8081" + base + "default/override");
         method.setQueryString(param);
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

   public void _testArrayDefault(String type, String value)
   {
      _testDefault("/array/", type, value);
   }

   @Test
   public void testGetBoolean()
   {
      _test("boolean", "true");
      resourceQueryPrimitives.doGet(true);
      resourceQueryPrimitiveWrappers.doGet(true);
      List<Boolean> list = new ArrayList<Boolean>();
      list.add(Boolean.TRUE);
      list.add(Boolean.TRUE);
      list.add(Boolean.TRUE);
      resourceQueryPrimitiveList.doGetBoolean(list);
      boolean[] array = {true, true, true};
      resourceQueryPrimitiveArray.doGetBoolean(array);
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
   public void testGetBooleanPrimitiveArrayDefault()
   {
      _testArrayDefault("boolean", "true");
   }

   @Test
   public void testGetByte()
   {
      _test("byte", "127");
      resourceQueryPrimitives.doGet((byte) 127);
      resourceQueryPrimitiveWrappers.doGet((byte) 127);
      List<Byte> list = new ArrayList<Byte>();
      list.add(new Byte((byte) 127));
      list.add(new Byte((byte) 127));
      list.add(new Byte((byte) 127));
      resourceQueryPrimitiveList.doGetByte(list);
      byte[] array = {(byte) 127, (byte) 127, (byte) 127};
      resourceQueryPrimitiveArray.doGetByte(array);
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
   public void testGetBytePrimitiveArrayDefault()
   {
      _testArrayDefault("byte", "127");
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
         method.setQueryString("int=abcdef");
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
         method.setQueryString("int=abcdef");
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
         method.setQueryString("int=abcdef&int=abcdef");
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

   @Path("/")
   public static interface IResourceQueryPrimitives
   {
      @GET
      @ProduceMime("application/boolean")
      String doGet(@QueryParam("boolean")boolean v);

      @GET
      @ProduceMime("application/byte")
      String doGet(@QueryParam("byte")byte v);

      @GET
      @ProduceMime("application/short")
      String doGet(@QueryParam("short")short v);

      @GET
      @ProduceMime("application/int")
      String doGet(@QueryParam("int")int v);

      @GET
      @ProduceMime("application/long")
      String doGet(@QueryParam("long")long v);

      @GET
      @ProduceMime("application/float")
      String doGet(@QueryParam("float")float v);

      @GET
      @ProduceMime("application/double")
      String doGet(@QueryParam("double")double v);
   }

   @Path("/wrappers")
   public static interface IResourceQueryPrimitiveWrappers
   {
      @GET
      @ProduceMime("application/boolean")
      String doGet(@QueryParam("boolean")Boolean v);

      @GET
      @ProduceMime("application/byte")
      String doGet(@QueryParam("byte")Byte v);

      @GET
      @ProduceMime("application/short")
      String doGet(@QueryParam("short")Short v);

      @GET
      @ProduceMime("application/int")
      String doGet(@QueryParam("int")Integer v);

      @GET
      @ProduceMime("application/long")
      String doGet(@QueryParam("long")Long v);

      @GET
      @ProduceMime("application/float")
      String doGet(@QueryParam("float")Float v);

      @GET
      @ProduceMime("application/double")
      String doGet(@QueryParam("double")Double v);
   }

   @Path("/list")
   public static interface IResourceQueryPrimitiveList
   {
      @GET
      @ProduceMime("application/boolean")
      String doGetBoolean(@QueryParam("boolean")List<Boolean> v);

      @GET
      @ProduceMime("application/byte")
      String doGetByte(@QueryParam("byte")List<Byte> v);

      @GET
      @ProduceMime("application/short")
      String doGetShort(@QueryParam("short")List<Short> v);

      @GET
      @ProduceMime("application/int")
      String doGetInteger(@QueryParam("int")List<Integer> v);

      @GET
      @ProduceMime("application/long")
      String doGetLong(@QueryParam("long")List<Long> v);

      @GET
      @ProduceMime("application/float")
      String doGetFloat(@QueryParam("float")List<Float> v);

      @GET
      @ProduceMime("application/double")
      String doGetDouble(@QueryParam("double")List<Double> v);
   }

   @Path("/array")
   public static interface IResourceQueryPrimitiveArray
   {
      @GET
      @ProduceMime("application/boolean")
      String doGetBoolean(@QueryParam("boolean")boolean[] v);

      @GET
      @ProduceMime("application/byte")
      String doGetByte(@QueryParam("byte")byte[] v);

      @GET
      @ProduceMime("application/short")
      String doGetShort(@QueryParam("short")short[] v);

      @GET
      @ProduceMime("application/int")
      String doGetInteger(@QueryParam("int")int[] v);

      @GET
      @ProduceMime("application/long")
      String doGetLong(@QueryParam("long")long[] v);

      @GET
      @ProduceMime("application/float")
      String doGetFloat(@QueryParam("float")float[] v);

      @GET
      @ProduceMime("application/double")
      String doGetDouble(@QueryParam("double")double[] v);
   }
}
