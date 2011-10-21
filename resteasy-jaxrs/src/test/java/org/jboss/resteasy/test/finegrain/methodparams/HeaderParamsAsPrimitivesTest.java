package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamsAsPrimitivesTest
{
   private static Dispatcher dispatcher;

   private static IResourceHeaderPrimitives resourceHeaderPrimitives;

   private static IResourceHeaderPrimitivesDefault resourceHeaderPrimitivesDefault;

   private static IResourceHeaderPrimitivesDefaultOverride resourceHeaderPrimitivesDefaultOverride;

   private static IResourceHeaderPrimitivesDefaultNull resourceHeaderPrimitivesDefaultNull;

   private static IResourceHeaderPrimitiveWrappers resourceHeaderPrimitiveWrappers;

   private static IResourceHeaderPrimitiveWrappersDefault resourceHeaderPrimitiveWrappersDefault;

   private static IResourceHeaderPrimitiveWrappersDefaultOverride resourceHeaderPrimitiveWrappersDefaultOverride;

   private static IResourceHeaderPrimitiveWrappersDefaultNull resourceHeaderPrimitiveWrappersDefaultNull;

   private static IResourceHeaderPrimitiveList resourceHeaderPrimitiveList;

   private static IResourceHeaderPrimitiveListDefault resourceHeaderPrimitiveListDefault;

   private static IResourceHeaderPrimitiveListDefaultOverride resourceHeaderPrimitiveListDefaultOverride;

   private static IResourceHeaderPrimitiveListDefaultNull resourceHeaderPrimitiveListDefaultNull;

   private static IResourceHeaderPrimitiveArray resourceHeaderPrimitiveArray;

   private static IResourceHeaderPrimitiveArrayDefault resourceHeaderPrimitiveArrayDefault;

   private static IResourceHeaderPrimitiveArrayDefaultOverride resourceHeaderPrimitiveArrayDefaultOverride;

   private static IResourceHeaderPrimitiveArrayDefaultNull resourceHeaderPrimitiveArrayDefaultNull;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitives.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitivesDefault.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitivesDefaultNull.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveWrappers.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveList.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveSet.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveSortedSet.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveListDefault.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveListDefaultOverride.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveArray.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveArrayDefault.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveArrayDefaultNull.class);
      dispatcher.getRegistry().addPerRequestResource(ResourceHeaderPrimitiveArrayDefaultOverride.class);
      resourceHeaderPrimitives = ProxyFactory.create(IResourceHeaderPrimitives.class, generateBaseUrl());
      resourceHeaderPrimitivesDefault = ProxyFactory.create(IResourceHeaderPrimitivesDefault.class,
              generateBaseUrl());
      resourceHeaderPrimitivesDefaultOverride = ProxyFactory.create(IResourceHeaderPrimitivesDefaultOverride.class,
              generateBaseUrl());
      resourceHeaderPrimitivesDefaultNull = ProxyFactory.create(IResourceHeaderPrimitivesDefaultNull.class,
              generateBaseUrl());
      resourceHeaderPrimitiveWrappers = ProxyFactory.create(IResourceHeaderPrimitiveWrappers.class,
              generateBaseUrl());
      resourceHeaderPrimitiveWrappersDefault = ProxyFactory.create(IResourceHeaderPrimitiveWrappersDefault.class,
              generateBaseUrl());
      resourceHeaderPrimitiveWrappersDefaultOverride = ProxyFactory.create(
              IResourceHeaderPrimitiveWrappersDefaultOverride.class, generateBaseUrl());
      resourceHeaderPrimitiveWrappersDefaultNull = ProxyFactory.create(
              IResourceHeaderPrimitiveWrappersDefaultNull.class, generateBaseUrl());
      resourceHeaderPrimitiveList = ProxyFactory.create(IResourceHeaderPrimitiveList.class, generateBaseUrl());
      resourceHeaderPrimitiveListDefault = ProxyFactory.create(IResourceHeaderPrimitiveListDefault.class,
              generateBaseUrl());
      resourceHeaderPrimitiveListDefaultOverride = ProxyFactory.create(
              IResourceHeaderPrimitiveListDefaultOverride.class, generateBaseUrl());
      resourceHeaderPrimitiveListDefaultNull = ProxyFactory.create(IResourceHeaderPrimitiveListDefaultNull.class,
              generateBaseUrl());
      resourceHeaderPrimitiveArray = ProxyFactory.create(IResourceHeaderPrimitiveArray.class, generateBaseUrl());
      resourceHeaderPrimitiveArrayDefault = ProxyFactory.create(IResourceHeaderPrimitiveArrayDefault.class,
              generateBaseUrl());
      resourceHeaderPrimitiveArrayDefaultOverride = ProxyFactory.create(
              IResourceHeaderPrimitiveArrayDefaultOverride.class, generateBaseUrl());
      resourceHeaderPrimitiveArrayDefaultNull = ProxyFactory.create(IResourceHeaderPrimitiveArrayDefaultNull.class,
              generateBaseUrl());

   }

   @AfterClass
   public static void after() throws Exception
   {
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitives.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitivesDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitivesDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitivesDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveWrappers.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveWrappersDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveWrappersDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveWrappersDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveList.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveSet.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveSortedSet.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveListDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveListDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveListDefaultOverride.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveArray.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveArrayDefault.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveArrayDefaultNull.class);
      dispatcher.getRegistry().removeRegistrations(ResourceHeaderPrimitiveArrayDefaultOverride.class);
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class ResourceHeaderPrimitives implements IResourceHeaderPrimitives
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") short v)
      {
         Assert.assertTrue(v == 32767);
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/null")
   public static class ResourceHeaderPrimitivesDefaultNull
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") boolean v)
      {
         Assert.assertEquals(false, v);
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") byte v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") short v)
      {
         Assert.assertTrue(0 == v);
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") int v)
      {
         Assert.assertEquals(0, v);
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") long v)
      {
         Assert.assertEquals(0l, v);
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") float v)
      {
         Assert.assertEquals(0.0f, v);
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") double v)
      {
         Assert.assertEquals(0.0d, v);
         return "content";
      }
   }

   @Path("/default")
   public static class ResourceHeaderPrimitivesDefault
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("true") boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("127") byte v)
      {
         System.out.println("2a");
         Assert.assertTrue(127 == v);
         System.out.println("2b");
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("32767") short v)
      {
         Assert.assertTrue(32767 == v);
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("2147483647") int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("9223372036854775807") long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("3.14159265") float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("3.14159265358979") double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/default/override")
   public static class ResourceHeaderPrimitivesDefaultOverride implements IResourceHeaderPrimitivesDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("false") boolean v)
      {
         Assert.assertEquals(true, v);
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("1") byte v)
      {
         Assert.assertTrue(127 == v);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("1") short v)
      {
         Assert.assertTrue(32767 == v);
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("1") int v)
      {
         Assert.assertEquals(2147483647, v);
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("1") long v)
      {
         Assert.assertEquals(9223372036854775807L, v);
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("0.0") float v)
      {
         Assert.assertEquals(3.14159265f, v);
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("0.0") double v)
      {
         Assert.assertEquals(3.14159265358979d, v);
         return "content";
      }
   }

   @Path("/wrappers")
   public static class ResourceHeaderPrimitiveWrappers implements IResourceHeaderPrimitiveWrappers
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());

         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/null")
   public static class ResourceHeaderPrimitiveWrappersDefaultNull
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") Boolean v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") Byte v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") Short v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") Integer v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") Long v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") Float v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") Double v)
      {
         Assert.assertEquals(null, v);
         return "content";
      }
   }

   @Path("/wrappers/default")
   public static class ResourceHeaderPrimitiveWrappersDefault
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("true") Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("127") Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());

         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("32767") Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("2147483647") Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("9223372036854775807") Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("3.14159265") Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("3.14159265358979") Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/wrappers/default/override")
   public static class ResourceHeaderPrimitiveWrappersDefaultOverride
           implements
           IResourceHeaderPrimitiveWrappersDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      public String doGet(@HeaderParam("boolean") @DefaultValue("false") Boolean v)
      {
         Assert.assertEquals(true, v.booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGet(@HeaderParam("byte") @DefaultValue("1") Byte v)
      {
         Assert.assertTrue(127 == v.byteValue());
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGet(@HeaderParam("short") @DefaultValue("1") Short v)
      {
         Assert.assertTrue(32767 == v.shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGet(@HeaderParam("int") @DefaultValue("1") Integer v)
      {
         Assert.assertEquals(2147483647, v.intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGet(@HeaderParam("long") @DefaultValue("1") Long v)
      {
         Assert.assertEquals(9223372036854775807L, v.longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGet(@HeaderParam("float") @DefaultValue("0.0") Float v)
      {
         Assert.assertEquals(3.14159265f, v.floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGet(@HeaderParam("double") @DefaultValue("0.0") Double v)
      {
         Assert.assertEquals(3.14159265358979d, v.doubleValue());
         return "content";
      }
   }

   @Path("/list")
   public static class ResourceHeaderPrimitiveList implements IResourceHeaderPrimitiveList
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         Assert.assertEquals(true, v.get(1).booleanValue());
         Assert.assertEquals(true, v.get(2).booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGetByte(@HeaderParam("byte") List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         Assert.assertTrue(127 == v.get(1).byteValue());
         Assert.assertTrue(127 == v.get(2).byteValue());
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         Assert.assertTrue(32767 == v.get(1).shortValue());
         Assert.assertTrue(32767 == v.get(2).shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGetInteger(@HeaderParam("int") List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         Assert.assertEquals(2147483647, v.get(1).intValue());
         Assert.assertEquals(2147483647, v.get(2).intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGetLong(@HeaderParam("long") List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(1).longValue());
         Assert.assertEquals(9223372036854775807L, v.get(2).longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGetFloat(@HeaderParam("float") List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         Assert.assertEquals(3.14159265f, v.get(1).floatValue());
         Assert.assertEquals(3.14159265f, v.get(2).floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGetDouble(@HeaderParam("double") List<Double> v)
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
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") List<Boolean> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGetByte(@HeaderParam("byte") List<Byte> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") List<Short> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGetInteger(@HeaderParam("int") List<Integer> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGetLong(@HeaderParam("long") List<Long> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGetFloat(@HeaderParam("float") List<Float> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGetDouble(@HeaderParam("double") List<Double> v)
      {
         Assert.assertEquals(0, v.size());
         return "content";
      }
   }

   @Path("/list/default")
   public static class ResourceHeaderPrimitiveListDefault
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("true") List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGetByte(@HeaderParam("byte") @DefaultValue("127") List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") @DefaultValue("32767") List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGetInteger(@HeaderParam("int") @DefaultValue("2147483647") List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGetLong(@HeaderParam("long") @DefaultValue("9223372036854775807") List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGetFloat(@HeaderParam("float") @DefaultValue("3.14159265") List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGetDouble(@HeaderParam("double") @DefaultValue("3.14159265358979") List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/list/default/override")
   public static class ResourceHeaderPrimitiveListDefaultOverride
           implements
           IResourceHeaderPrimitiveListDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") List<Boolean> v)
      {
         Assert.assertEquals(true, v.get(0).booleanValue());
         return "content";
      }

      @GET
      @Produces("application/byte")
      public String doGetByte(@HeaderParam("byte") @DefaultValue("0") List<Byte> v)
      {
         Assert.assertTrue(127 == v.get(0).byteValue());
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") @DefaultValue("0") List<Short> v)
      {
         Assert.assertTrue(32767 == v.get(0).shortValue());
         return "content";
      }

      @GET
      @Produces("application/int")
      public String doGetInteger(@HeaderParam("int") @DefaultValue("0") List<Integer> v)
      {
         Assert.assertEquals(2147483647, v.get(0).intValue());
         return "content";
      }

      @GET
      @Produces("application/long")
      public String doGetLong(@HeaderParam("long") @DefaultValue("0") List<Long> v)
      {
         Assert.assertEquals(9223372036854775807L, v.get(0).longValue());
         return "content";
      }

      @GET
      @Produces("application/float")
      public String doGetFloat(@HeaderParam("float") @DefaultValue("0.0") List<Float> v)
      {
         Assert.assertEquals(3.14159265f, v.get(0).floatValue());
         return "content";
      }

      @GET
      @Produces("application/double")
      public String doGetDouble(@HeaderParam("double") @DefaultValue("0.0") List<Double> v)
      {
         Assert.assertEquals(3.14159265358979d, v.get(0).doubleValue());
         return "content";
      }
   }

   @Path("/set")
   public static class ResourceHeaderPrimitiveSet implements IResourceHeaderPrimitiveSet
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("header") Set<String> v)
      {
         Assert.assertEquals(2, v.size());
         Assert.assertTrue(v.contains("one"));
         Assert.assertTrue(v.contains("two"));
         return "content";
      }
   }

   @Path("/sortedset")
   public static class ResourceHeaderPrimitiveSortedSet implements IResourceHeaderPrimitiveSortedSet
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("header") SortedSet<String> v)
      {
         Assert.assertEquals(2, v.size());
         Assert.assertTrue(v.contains("one"));
         Assert.assertTrue(v.contains("two"));
         return "content";
      }
   }

   @Path("/array")
   public static class ResourceHeaderPrimitiveArray implements IResourceHeaderPrimitiveArray
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         Assert.assertEquals(true, v[1]);
         Assert.assertEquals(true, v[2]);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") short[] v)
      {
         Assert.assertTrue(32767 == v[0]);
         Assert.assertTrue(32767 == v[0]);
         Assert.assertTrue(32767 == v[0]);
         return "content";
      }
   }

   @Path("/array/default/null")
   public static class ResourceHeaderPrimitiveArrayDefaultNull
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") boolean[] v)
      {
         Assert.assertEquals(0, v.length);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") short[] v)
      {
         Assert.assertEquals(0, v.length);
         return "content";
      }
   }

   @Path("/array/default")
   public static class ResourceHeaderPrimitiveArrayDefault
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("true") boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("short") @DefaultValue("32767") short[] v)
      {
         Assert.assertTrue(32767 == v[0]);
         return "content";
      }
   }

   @Path("/array/default/override")
   public static class ResourceHeaderPrimitiveArrayDefaultOverride
           implements
           IResourceHeaderPrimitiveArrayDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v)
      {
         Assert.assertEquals(true, v[0]);
         return "content";
      }

      @GET
      @Produces("application/short")
      public String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v)
      {
         Assert.assertTrue(32767 == v[0]);
         return "content";
      }
   }

   public void _test(String type, String value)
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         request.header(type, value);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/wrappers"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         request.header(type, value);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
     
      {
         ClientRequest request = new ClientRequest(generateURL("/list"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         request.header(type, value);
         request.header(type, value);
         request.header(type, value);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public void _testDefault(String base, String type, String value)
   {
      {
         System.out.println("DOING _testDefault() 1");
         ClientRequest request = new ClientRequest(generateURL("" + base + "default/null"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         System.out.println("DOING _testDefault() 2");
         ClientRequest request = new ClientRequest(generateURL("" + base + "default"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }

      {
         System.out.println("DOING _testDefault() 3");
         ClientRequest request = new ClientRequest(generateURL("" + base + "default/override"));
         request.header(HttpHeaderNames.ACCEPT, "application/" + type);
         request.header(type, value);
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
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
   public void testSet()
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/set"));
         request.header(HttpHeaderNames.ACCEPT, "application/boolean");
         request.header("header", "one");
         request.header("header", "one");
         request.header("header", "one");
         request.header("header", "two");
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         IResourceHeaderPrimitiveSet setClient = ProxyFactory.create(IResourceHeaderPrimitiveSet.class,
               generateBaseUrl());
         HashSet<String> set = new HashSet<String>();
         set.add("one");
         set.add("two");
         setClient.doGetBoolean(set);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/sortedset"));
         request.header(HttpHeaderNames.ACCEPT, "application/boolean");
         request.header("header", "one");
         request.header("header", "one");
         request.header("header", "one");
         request.header("header", "two");
         ClientResponse<?> response;
         try
         {
            response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
           //response.releaseConnection();
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
            executor.getHttpClient().getConnectionManager().shutdown();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         };
         IResourceHeaderPrimitiveSortedSet setClient = ProxyFactory.create(IResourceHeaderPrimitiveSortedSet.class,
               generateBaseUrl());
         TreeSet<String> set = new TreeSet<String>();
         set.add("one");
         set.add("two");
         setClient.doGetBoolean(set);
      }
   }

   @Test
   public void testGetBoolean()
   {
      _test("boolean", "true");
      resourceHeaderPrimitives.doGet(true);
      resourceHeaderPrimitiveWrappers.doGet(Boolean.TRUE);
      ArrayList<Boolean> list = new ArrayList<Boolean>();
      list.add(Boolean.TRUE);
      list.add(Boolean.TRUE);
      list.add(Boolean.TRUE);
      resourceHeaderPrimitiveList.doGetBoolean(list);
      boolean[] array =
              {true, true, true};
      resourceHeaderPrimitiveArray.doGetBoolean(array);
   }

   @Test
   public void testGetBooleanPrimitivesDefault()
   {
      _testDefault("boolean", "true");
      resourceHeaderPrimitivesDefault.doGetBoolean();
      resourceHeaderPrimitivesDefaultNull.doGetBoolean();
      resourceHeaderPrimitivesDefaultOverride.doGet(true);
   }

   @Test
   public void testGetBooleanPrimitiveWrapperDefault()
   {
      System.out.println("entering testGetBooleanPrimitiveWrapperDefault()");
      _testWrappersDefault("boolean", "true");
      System.out.println("CALLING resourceHeaderPrimitiveWrappersDefault.doGetBoolean()");
      resourceHeaderPrimitiveWrappersDefault.doGetBoolean();
      System.out.println("CALLING resourceHeaderPrimitiveWrappersDefaultNull.doGetBoolean()");
      resourceHeaderPrimitiveWrappersDefaultNull.doGetBoolean();
      System.out.println("CALLING resourceHeaderPrimitiveWrappersDefaultOverride.doGet()");
      resourceHeaderPrimitiveWrappersDefaultOverride.doGet(Boolean.TRUE);
   }

   @Test
   public void testGetBooleanPrimitiveListDefault()
   {
      _testListDefault("boolean", "true");
      resourceHeaderPrimitiveListDefault.doGetBoolean();
      resourceHeaderPrimitiveListDefaultNull.doGetBoolean();
      List<Boolean> list = new ArrayList<Boolean>();
      list.add(Boolean.TRUE);
      resourceHeaderPrimitiveListDefaultOverride.doGetBoolean(list);
      resourceHeaderPrimitiveArrayDefault.doGetBoolean();
      resourceHeaderPrimitiveArrayDefaultNull.doGetBoolean();
      boolean[] array =
              {true};
      resourceHeaderPrimitiveArrayDefaultOverride.doGetBoolean(array);
   }

   @Test
   public void testGetByte()
   {
      _test("byte", "127");
      resourceHeaderPrimitives.doGet((byte) 127);
      resourceHeaderPrimitiveWrappers.doGet(new Byte((byte) 127));
      ArrayList<Byte> list = new ArrayList<Byte>();
      list.add(new Byte((byte) 127));
      list.add(new Byte((byte) 127));
      list.add(new Byte((byte) 127));
      resourceHeaderPrimitiveList.doGetByte(list);
   }

   @Test
   public void testGetBytePrimitivesDefault()
   {
      System.out.println("1");
      _testDefault("byte", "127");
      System.out.println("2");
      resourceHeaderPrimitivesDefault.doGetByte();
      System.out.println("3");
      resourceHeaderPrimitivesDefaultNull.doGetByte();
      System.out.println("4");
      resourceHeaderPrimitivesDefaultOverride.doGet((byte) 127);
      System.out.println("5");
   }

   @Test
   public void testGetBytePrimitiveWrappersDefault()
   {
      _testWrappersDefault("byte", "127");
      resourceHeaderPrimitiveWrappersDefault.doGetByte();
      resourceHeaderPrimitiveWrappersDefaultNull.doGetByte();
      resourceHeaderPrimitiveWrappersDefaultOverride.doGet(new Byte((byte) 127));
   }

   @Test
   public void testGetBytePrimitiveListDefault()
   {
      _testListDefault("byte", "127");
      resourceHeaderPrimitiveListDefault.doGetByte();
      resourceHeaderPrimitiveListDefaultNull.doGetByte();
      List<Byte> list = new ArrayList<Byte>();
      list.add(new Byte((byte) 127));
      resourceHeaderPrimitiveListDefaultOverride.doGetByte(list);
   }

   @Test
   public void testGetShort()
   {
      _test("short", "32767");
      resourceHeaderPrimitives.doGet((short) 32767);
      resourceHeaderPrimitiveWrappers.doGet(new Short((short) 32767));
      ArrayList<Short> list = new ArrayList<Short>();
      list.add(new Short((short) 32767));
      list.add(new Short((short) 32767));
      list.add(new Short((short) 32767));
      resourceHeaderPrimitiveList.doGetShort(list);
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
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.ACCEPT, "application/int");
      request.header("int", "abcdef");
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(400, response.getStatus());
        //response.releaseConnection();
         ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
         executor.getHttpClient().getConnectionManager().shutdown();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testBadPrimitiveWrapperValue()
   {
      ClientRequest request = new ClientRequest(generateURL("/wrappers"));
      request.header(HttpHeaderNames.ACCEPT, "application/int");
      request.header("int", "abcdef");
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(400, response.getStatus());
        //response.releaseConnection();
         ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
         executor.getHttpClient().getConnectionManager().shutdown();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }  
   }

   @Test
   public void testBadPrimitiveListValue()
   {
      ClientRequest request = new ClientRequest(generateURL("/list"));
      request.header(HttpHeaderNames.ACCEPT, "application/int");
      request.header("int", "abcdef");
      request.header("int", "abcdef");
      request.header("int", "abcdef");
      ClientResponse<?> response;
      try
      {
         response = request.get();
         Assert.assertEquals(400, response.getStatus());
        //response.releaseConnection();
         ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
         executor.getHttpClient().getConnectionManager().shutdown();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Path("/")
   public static interface IResourceHeaderPrimitives
   {
      @GET
      @Produces("application/boolean")
      String doGet(@HeaderParam("boolean") boolean v);

      @GET
      @Produces("application/byte")
      String doGet(@HeaderParam("byte") byte v);

      @GET
      @Produces("application/short")
      String doGet(@HeaderParam("short") short v);

      @GET
      @Produces("application/int")
      String doGet(@HeaderParam("int") int v);

      @GET
      @Produces("application/long")
      String doGet(@HeaderParam("long") long v);

      @GET
      @Produces("application/float")
      String doGet(@HeaderParam("float") float v);

      @GET
      @Produces("application/double")
      String doGet(@HeaderParam("double") double v);
   }

   @Path("/default/null")
   public static interface IResourceHeaderPrimitivesDefaultNull
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInt();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGet();
   }

   @Path("/default")
   public static interface IResourceHeaderPrimitivesDefault
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInt();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGetDouble();
   }

   @Path("/default/override")
   public static interface IResourceHeaderPrimitivesDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      String doGet(@HeaderParam("boolean") @DefaultValue("false") boolean v);

      @GET
      @Produces("application/byte")
      String doGet(@HeaderParam("byte") @DefaultValue("1") byte v);

      @GET
      @Produces("application/short")
      String doGet(@HeaderParam("short") @DefaultValue("1") short v);

      @GET
      @Produces("application/int")
      String doGet(@HeaderParam("int") @DefaultValue("1") int v);

      @GET
      @Produces("application/long")
      String doGet(@HeaderParam("long") @DefaultValue("1") long v);

      @GET
      @Produces("application/float")
      String doGet(@HeaderParam("float") @DefaultValue("0.0") float v);

      @GET
      @Produces("application/double")
      String doGet(@HeaderParam("double") @DefaultValue("0.0") double v);
   }

   @Path("/wrappers")
   public static interface IResourceHeaderPrimitiveWrappers
   {
      @GET
      @Produces("application/boolean")
      String doGet(@HeaderParam("boolean") Boolean v);

      @GET
      @Produces("application/byte")
      String doGet(@HeaderParam("byte") Byte v);

      @GET
      @Produces("application/short")
      String doGet(@HeaderParam("short") Short v);

      @GET
      @Produces("application/int")
      String doGet(@HeaderParam("int") Integer v);

      @GET
      @Produces("application/long")
      String doGet(@HeaderParam("long") Long v);

      @GET
      @Produces("application/float")
      String doGet(@HeaderParam("float") Float v);

      @GET
      @Produces("application/double")
      String doGet(@HeaderParam("double") Double v);
   }

   @Path("/wrappers/default/null")
   public static interface IResourceHeaderPrimitiveWrappersDefaultNull
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInt();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGetDouble();
   }

   @Path("/wrappers/default")
   public static interface IResourceHeaderPrimitiveWrappersDefault
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInteger();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGetDouble();
   }

   @Path("/wrappers/default/override")
   public static interface IResourceHeaderPrimitiveWrappersDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      String doGet(@HeaderParam("boolean") @DefaultValue("false") Boolean v);

      @GET
      @Produces("application/byte")
      String doGet(@HeaderParam("byte") @DefaultValue("1") Byte v);

      @GET
      @Produces("application/short")
      String doGet(@HeaderParam("short") @DefaultValue("1") Short v);

      @GET
      @Produces("application/int")
      String doGet(@HeaderParam("int") @DefaultValue("1") Integer v);

      @GET
      @Produces("application/long")
      String doGet(@HeaderParam("long") @DefaultValue("1") Long v);

      @GET
      @Produces("application/float")
      String doGet(@HeaderParam("float") @DefaultValue("0.0") Float v);

      @GET
      @Produces("application/double")
      String doGet(@HeaderParam("double") @DefaultValue("0.0") Double v);
   }

   @Path("/set")
   public static interface IResourceHeaderPrimitiveSet
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("header") Set<String> v);
   }

   @Path("/sortedset")
   public static interface IResourceHeaderPrimitiveSortedSet
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("header") SortedSet<String> v);
   }

   @Path("/list")
   public static interface IResourceHeaderPrimitiveList
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("boolean") List<Boolean> v);

      @GET
      @Produces("application/byte")
      String doGetByte(@HeaderParam("byte") List<Byte> v);

      @GET
      @Produces("application/short")
      String doGetShort(@HeaderParam("short") List<Short> v);

      @GET
      @Produces("application/int")
      String doGetInteger(@HeaderParam("int") List<Integer> v);

      @GET
      @Produces("application/long")
      String doGetLong(@HeaderParam("long") List<Long> v);

      @GET
      @Produces("application/float")
      String doGetFloat(@HeaderParam("float") List<Float> v);

      @GET
      @Produces("application/double")
      String doGetDouble(@HeaderParam("double") List<Double> v);
   }

   @Path("/list/default/null")
   public static interface IResourceHeaderPrimitiveListDefaultNull
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInteger();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGetDouble();
   }

   @Path("/list/default")
   public static interface IResourceHeaderPrimitiveListDefault
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/byte")
      String doGetByte();

      @GET
      @Produces("application/short")
      String doGetShort();

      @GET
      @Produces("application/int")
      String doGetInteger();

      @GET
      @Produces("application/long")
      String doGetLong();

      @GET
      @Produces("application/float")
      String doGetFloat();

      @GET
      @Produces("application/double")
      String doGetDouble();
   }

   @Path("/list/default/override")
   public static interface IResourceHeaderPrimitiveListDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") List<Boolean> v);

      @GET
      @Produces("application/byte")
      String doGetByte(@HeaderParam("byte") @DefaultValue("0") List<Byte> v);

      @GET
      @Produces("application/short")
      String doGetShort(@HeaderParam("short") @DefaultValue("0") List<Short> v);

      @GET
      @Produces("application/int")
      String doGetInteger(@HeaderParam("int") @DefaultValue("0") List<Integer> v);

      @GET
      @Produces("application/long")
      String doGetLong(@HeaderParam("long") @DefaultValue("0") List<Long> v);

      @GET
      @Produces("application/float")
      String doGetFloat(@HeaderParam("float") @DefaultValue("0.0") List<Float> v);

      @GET
      @Produces("application/double")
      String doGetDouble(@HeaderParam("double") @DefaultValue("0.0") List<Double> v);
   }

   @Path("/array")
   public static interface IResourceHeaderPrimitiveArray
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("boolean") boolean[] v);

      @GET
      @Produces("application/short")
      String doGetShort(@HeaderParam("short") short[] v);
   }

   @Path("/array/default/null")
   public static interface IResourceHeaderPrimitiveArrayDefaultNull
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/short")
      String doGetShort();
   }

   @Path("/array/default")
   public static interface IResourceHeaderPrimitiveArrayDefault
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean();

      @GET
      @Produces("application/short")
      String doGetShort();
   }

   @Path("/array/default/override")
   public static interface IResourceHeaderPrimitiveArrayDefaultOverride
   {
      @GET
      @Produces("application/boolean")
      String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v);

      @GET
      @Produces("application/short")
      String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v);
   }
}
