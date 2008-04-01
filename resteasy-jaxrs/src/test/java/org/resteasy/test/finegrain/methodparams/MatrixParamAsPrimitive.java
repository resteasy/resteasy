package org.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamAsPrimitive
{
   private static HttpClient client = new HttpClient();
   private static Dispatcher dispatcher;

   //private static IResourceUriBoolean resourceUriBoolean;


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitives.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitivesDefault.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitivesDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveWrappers.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveList.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveListDefault.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveListDefaultOverride.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveArray.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveArrayDefault.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveArrayDefaultNull.class);
      dispatcher.getRegistry().addResource(ResourceMatrixPrimitiveArrayDefaultOverride.class);
      //resourceUriBoolean = ProxyFactory.create(IResourceUriBoolean.class, "http://localhost:8081");
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class ResourceMatrixPrimitives
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/null")
   public static class ResourceMatrixPrimitivesDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean")boolean v)
      {
         Assert.assertEquals(false, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte")byte v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short")short v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int")int v)
      {
         Assert.assertEquals(0, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long")long v)
      {
         Assert.assertEquals(0l, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float")float v)
      {
         Assert.assertEquals(0.0f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double")double v)
      {
         Assert.assertEquals(0.0d, v);
         return "content";
      }
   }

   @Path("/default")
   public static class ResourceMatrixPrimitivesDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean") @DefaultValue("true")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte") @DefaultValue("127")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short") @DefaultValue("32767")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int") @DefaultValue("2147483647")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long") @DefaultValue("9223372036854775807")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float") @DefaultValue("3.14159265")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double") @DefaultValue("3.14159265358979")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/override")
   public static class ResourceMatrixPrimitivesDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean") @DefaultValue("false")boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte") @DefaultValue("1")byte v)
      {
         Assert.assertTrue((byte) 127 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short") @DefaultValue("1")short v)
      {
         Assert.assertTrue((short) 32767 == v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int") @DefaultValue("1")int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long") @DefaultValue("1")long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float") @DefaultValue("0.0")float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double") @DefaultValue("0.0")double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/wrappers")
   public static class ResourceMatrixPrimitiveWrappers
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/null")
   public static class ResourceMatrixPrimitiveWrappersDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean")Boolean v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte")Byte v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short")Short v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int")Integer v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long")Long v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float")Float v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double")Double v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/wrappers/default")
   public static class ResourceMatrixPrimitiveWrappersDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean") @DefaultValue("true")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte") @DefaultValue("127")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short") @DefaultValue("32767")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int") @DefaultValue("2147483647")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long") @DefaultValue("9223372036854775807")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float") @DefaultValue("3.14159265")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double") @DefaultValue("3.14159265358979")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/override")
   public static class ResourceMatrixPrimitiveWrappersDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGet(@MatrixParam("boolean") @DefaultValue("false")Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGet(@MatrixParam("byte") @DefaultValue("1")Byte v)
      {
         Assert.assertTrue((byte) 127 == v.byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGet(@MatrixParam("short") @DefaultValue("1")Short v)
      {
         Assert.assertTrue((short) 32767 == v.shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGet(@MatrixParam("int") @DefaultValue("1")Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGet(@MatrixParam("long") @DefaultValue("1")Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGet(@MatrixParam("float") @DefaultValue("0.0")Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGet(@MatrixParam("double") @DefaultValue("0.0")Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/list")
   public static class ResourceMatrixPrimitiveList
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         Assert.assertEquals(true, v.get(1).booleanValue());
         Assert.assertEquals(true, v.get(2).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         Assert.assertTrue((byte) 127 == v.get(1).byteValue());
         Assert.assertTrue((byte) 127 == v.get(2).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         Assert.assertTrue((short) 32767 == v.get(1).shortValue());
         Assert.assertTrue((short) 32767 == v.get(2).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         Assert.assertEquals(2147483647, v.get(1).intValue());
         Assert.assertEquals(2147483647, v.get(2).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(1).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(2).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         Assert.assertEquals(3.14159265f, v.get(1).floatValue());
         Assert.assertEquals(3.14159265f, v.get(2).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(1).doubleValue());
         Assert.assertEquals(3.14159265358979d, v.get(2).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/null")
   public static class ResourceMatrixPrimitiveListDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean")List<Boolean> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte")List<Byte> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short")List<Short> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int")List<Integer> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long")List<Long> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float")List<Float> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double")List<Double> v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/list/default")
   public static class ResourceMatrixPrimitiveListDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("true")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte") @DefaultValue("127")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short") @DefaultValue("32767")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int") @DefaultValue("2147483647")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long") @DefaultValue("9223372036854775807")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float") @DefaultValue("3.14159265")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double") @DefaultValue("3.14159265358979")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/override")
   public static class ResourceMatrixPrimitiveListDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("false")List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte") @DefaultValue("0")List<Byte> v)
      {
         Assert.assertTrue((byte) 127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short") @DefaultValue("0")List<Short> v)
      {
         Assert.assertTrue((short) 32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int") @DefaultValue("0")List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long") @DefaultValue("0")List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float") @DefaultValue("0.0")List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double") @DefaultValue("0.0")List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/array")
   public static class ResourceMatrixPrimitiveArray
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         Assert.assertEquals(true, v[1]);
         Assert.assertEquals(true, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         Assert.assertTrue((byte) 127 == v[1]);
         Assert.assertTrue((byte) 127 == v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short")short[] v)
      {
         Assert.assertTrue(32767 == v[0]);
         Assert.assertTrue(32767 == v[1]);
         Assert.assertTrue(32767 == v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         Assert.assertEquals(2147483647, v[1]);
         Assert.assertEquals(2147483647, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         Assert.assertEquals(9223372036854775807L, v[1]);
         Assert.assertEquals(9223372036854775807L, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         Assert.assertEquals(3.14159265f, v[1]);
         Assert.assertEquals(3.14159265f, v[2]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         Assert.assertEquals(3.14159265358979d, v[1]);
         Assert.assertEquals(3.14159265358979d, v[2]);
         return "content";
      }
   }

   @Path("/array/default/null")
   public static class ResourceMatrixPrimitiveArrayDefaultNull
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean")boolean[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte")byte[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short")short[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int")int[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long")long[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float")float[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double")double[] v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/array/default")
   public static class ResourceMatrixPrimitiveArrayDefault
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("true")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte") @DefaultValue("127")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short") @DefaultValue("32767")short[] v)
      {
         Assert.assertTrue((short) 32767 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int") @DefaultValue("2147483647")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long") @DefaultValue("9223372036854775807")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float") @DefaultValue("3.14159265")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double") @DefaultValue("3.14159265358979")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         return "content";
      }
   }

   @Path("/array/default/override")
   public static class ResourceMatrixPrimitiveArrayDefaultOverride
   {
      @GET
      @ProduceMime("application/boolean")
      public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("false")boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/byte")
      public String doGetByte(@MatrixParam("byte") @DefaultValue("0")byte[] v)
      {
         Assert.assertTrue((byte) 127 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/short")
      public String doGetShort(@MatrixParam("short") @DefaultValue("0")short[] v)
      {
         Assert.assertTrue((short) 32767 == v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/int")
      public String doGetInteger(@MatrixParam("int") @DefaultValue("0")int[] v)
      {
         Assert.assertEquals(2147483647, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/long")
      public String doGetLong(@MatrixParam("long") @DefaultValue("0")long[] v)
      {
         Assert.assertEquals(9223372036854775807L, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/float")
      public String doGetFloat(@MatrixParam("float") @DefaultValue("0.0")float[] v)
      {
         Assert.assertEquals(3.14159265f, v[0]);
         return "content";
      }

      @GET
      @ProduceMime("application/double")
      public String doGetDouble(@MatrixParam("double") @DefaultValue("0.0")double[] v)
      {
         Assert.assertEquals(3.14159265358979d, v[0]);
         return "content";
      }
   }


   public void _test(String type, String value)
   {
      String param = ";" + type + "=" + value;
      {
         GetMethod method = new GetMethod("http://localhost:8081/" + param);
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
         GetMethod method = new GetMethod("http://localhost:8081/wrappers" + param);
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
         GetMethod method = new GetMethod("http://localhost:8081/list" + param + param + param);
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
         GetMethod method = new GetMethod("http://localhost:8081/array" + param + param + param);
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

      String param = ";" + type + "=" + value;
      {
         GetMethod method = new GetMethod("http://localhost:8081" + base + "default/override" + param);
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
      _testArrayDefault("boolean", "true");
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
      _testArrayDefault("short", "32767");
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
      _testArrayDefault("int", "2147483647");
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
      _testArrayDefault("long", "9223372036854775807");
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
      _testArrayDefault("float", "3.14159265");
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
      _testArrayDefault("double", "3.14159265358979");
   }

   @Test
   public void testBadPrimitiveValue()
   {
      {
         GetMethod method = new GetMethod("http://localhost:8081/;int=abcdef");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
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
         GetMethod method = new GetMethod("http://localhost:8081/wrappers;int=abcdef");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
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
         GetMethod method = new GetMethod("http://localhost:8081/list;int=abcdef;int=abcdef");
         method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/int");
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
