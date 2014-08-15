package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMapTest
{
   @Test
   public void testMatching()
   {
      MediaTypeMap<String> map = new MediaTypeMap<String>();
      String defaultPlainText = "defaultPlainText";
      map.add(new MediaType("text", "plain"), defaultPlainText);
      String jaxb = "jaxb";
      map.add(new MediaType("text", "xml"), jaxb);
      String wildcard = "wildcard";
      map.add(new MediaType("*", "*"), wildcard);
      String allText = "allText";
      map.add(new MediaType("text", "*"), allText);
      String allXML = "allXML";
      map.add(new MediaType("text", "*+xml"), allXML);
      String app = "app";
      map.add(new MediaType("application", "*"), app);

      List<String> list = map.getPossible(new MediaType("text", "plain"));
      Assert.assertNotNull(list);
      Assert.assertEquals(3, list.size());
      Assert.assertTrue(list.get(0) == defaultPlainText);
      Assert.assertTrue(list.get(1) == allText);
      Assert.assertTrue(list.get(2) == wildcard);

      list = map.getPossible(new MediaType("*", "*"));
      Assert.assertNotNull(list);
      Assert.assertEquals(6, list.size());
      Assert.assertTrue(list.get(0), list.get(0) == defaultPlainText || list.get(0) == jaxb);
      Assert.assertTrue(list.get(1), list.get(1) == defaultPlainText || list.get(1) == jaxb);
      Assert.assertTrue(list.get(2), list.get(2) == allXML);
      Assert.assertTrue(list.get(3), list.get(3) == allText || list.get(3) == app);
      Assert.assertTrue(list.get(4), list.get(4) == allText || list.get(4) == app);
      Assert.assertTrue(list.get(5), list.get(5) == wildcard);

      list = map.getPossible(new MediaType("text", "*"));
      Assert.assertNotNull(list);
      Assert.assertEquals(5, list.size());
      Assert.assertTrue(list.get(0), list.get(0) == defaultPlainText || list.get(0) == jaxb);
      Assert.assertTrue(list.get(1), list.get(1) == defaultPlainText || list.get(1) == jaxb);
      Assert.assertTrue(list.get(2), list.get(2) == allXML);
      Assert.assertTrue(list.get(3), list.get(3) == allText);
      Assert.assertTrue(list.get(4), list.get(4) == wildcard);

      list = map.getPossible(new MediaType("text", "xml"));
      Assert.assertNotNull(list);
      Assert.assertEquals(4, list.size());
      Assert.assertTrue(list.get(0) == jaxb);
      Assert.assertTrue(list.get(1) == allXML);
      Assert.assertTrue(list.get(2) == allText);
      Assert.assertTrue(list.get(3) == wildcard);
   }

   @Provider
   @Produces("text/plain")
   public static class PlainTextWriter implements MessageBodyWriter
   {
      public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return 0;
      }

      public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
      }
   }

   @Provider
   @Produces("text/plain")
   public static class IntegerPlainTextWriter implements MessageBodyWriter<Integer>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      public long getSize(Integer integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return 0;
      }

      public void writeTo(Integer integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
      }
   }

   public static class Base<T> implements MessageBodyWriter<T>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      public long getSize(T integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return 0;
      }

      public void writeTo(T integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
      {
      }
   }

   @Provider
   @Produces("text/plain")
   public static class Concrete extends Base<Double>
   {
   }


   public static class Base2<X> extends Base<X>
   {
   }

   @Provider
   @Produces("text/plain")
   public static class Concrete2 extends Base<Boolean>
   {
   }

   public static class BaseMultiple<V, X> extends Base<X>
   {
   }

   public static class ConcreteMultiple extends BaseMultiple<String, Short>
   {
   }


   @Test
   public void testTypes()
   {
      Assert.assertNull(Types.getTemplateParameterOfInterface(PlainTextWriter.class, MessageBodyWriter.class));
      Assert.assertEquals(Integer.class, Types.getTemplateParameterOfInterface(IntegerPlainTextWriter.class, MessageBodyWriter.class));
      Assert.assertEquals(Double.class, Types.getTemplateParameterOfInterface(Concrete.class, MessageBodyWriter.class));
      Assert.assertEquals(Boolean.class, Types.getTemplateParameterOfInterface(Concrete2.class, MessageBodyWriter.class));
      Assert.assertEquals(Short.class, Types.getTemplateParameterOfInterface(ConcreteMultiple.class, MessageBodyWriter.class));
   }

   @Test
   public void testMatching2()
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      RegisterBuiltin.register(factory);

      MessageBodyWriter<Integer> writer = factory.getMessageBodyWriter(Integer.class, null, null, new MediaType("text", "plain"));
      Assert.assertNotNull(writer);
      Assert.assertEquals(writer.getClass(), DefaultTextPlain.class);
   }

   @Test
   public void testUserPrecendence1() throws Exception
   {
      // Register Built In first
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      RegisterBuiltin.register(factory);

      factory.addMessageBodyWriter(new PlainTextWriter());

      // Test that application providers take precedence over builtin
      verifyPlainWriter(factory);

      factory.addMessageBodyWriter(new IntegerPlainTextWriter());
      verifyIntegerWriter(factory);

   }

   @Test
   public void testUserPrecendence2() throws Exception
   {
      // register PlainTextWriter first
      ResteasyProviderFactory factory = new ResteasyProviderFactory();

      factory.addMessageBodyWriter(new PlainTextWriter());
      RegisterBuiltin.register(factory);

      verifyPlainWriter(factory);

      factory.addMessageBodyWriter(new IntegerPlainTextWriter());
      verifyIntegerWriter(factory);

   }

   @Test
   public void testUserPrecendence3() throws Exception
   {
      // register PlainTextWriter first
      ResteasyProviderFactory factory = new ResteasyProviderFactory();

      factory.addMessageBodyWriter(new IntegerPlainTextWriter());
      factory.addMessageBodyWriter(new PlainTextWriter());
      RegisterBuiltin.register(factory);

      verifyIntegerWriter(factory);

   }

   private void verifyPlainWriter(ResteasyProviderFactory factory)
   {
      MessageBodyWriter writer2 = factory.getMessageBodyWriter(Integer.class, null, null, MediaType.TEXT_PLAIN_TYPE);
      Assert.assertNotNull(writer2);
      Assert.assertTrue(writer2 instanceof PlainTextWriter);
   }

   private void verifyIntegerWriter(ResteasyProviderFactory factory)
   {
      MessageBodyWriter writer2;// Test that type specific template providers take precedence over others
      writer2 = factory.getMessageBodyWriter(Integer.class, null, null, MediaType.TEXT_PLAIN_TYPE);
      Assert.assertNotNull(writer2);
      Assert.assertTrue(writer2.getClass().getName(), writer2 instanceof IntegerPlainTextWriter);
   }


}
