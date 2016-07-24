package org.jboss.resteasy.test.nextgen.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * RESTEASY-1345
 * 
 * @author Nicolas NESMON
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date April 27, 2016
 */
public class ConfigurationInheritenceTest extends ResteasyProviderFactory
{
   private static TestFeature2 testFeature2 = new TestFeature2();
   private static TestFeature4 testFeature4 = new TestFeature4();
   private static TestFeature6 testFeature6 = new TestFeature6();
   private static TestFilter2 testFilter2 = new TestFilter2();
   private static TestFilter4 testFilter4 = new TestFilter4();
   private static TestFilter6 testFilter6 = new TestFilter6();
   private static TestMessageBodyReader2 testMessageBodyReader2 = new TestMessageBodyReader2();
   private static TestMessageBodyReader4 testMessageBodyReader4 = new TestMessageBodyReader4();
   private static TestMessageBodyReader6 testMessageBodyReader6 = new TestMessageBodyReader6();
   
   public static class TestFeature1 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFeature2 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFeature3 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFeature4 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFeature5 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFeature6 implements Feature
   {
      @Override
      public boolean configure(FeatureContext context)
      {
         return true;
      }
   }
   
   public static class TestFilter1 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter1.class);
      }
   }

   public static class TestFilter2 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter2.class);
      }
   }
   
   public static class TestFilter3 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter1.class);
      }
   }

   public static class TestFilter4 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter2.class);
      }
   }
   
   public static class TestFilter5 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter1.class);
      }
   }

   public static class TestFilter6 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(TestFilter2.class);
      }
   }
   public static class TestMessageBodyReader1 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }
   
   public static class TestMessageBodyReader2 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }

   public static class TestMessageBodyReader3 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }
   
   public static class TestMessageBodyReader4 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }
   

   public static class TestMessageBodyReader5 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }
   
   public static class TestMessageBodyReader6 implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }
   
   @Test
   public void testClientBuilderToClient()
   {
      ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
      clientBuilder.register(TestFeature1.class);
      clientBuilder.register(testFeature2);
      clientBuilder.register(new TestFilter1());
      clientBuilder.register(testFilter2);
      clientBuilder.register(new TestMessageBodyReader1());
      clientBuilder.register(testMessageBodyReader2);  
      clientBuilder.property("property1", "value1");
      
      Client client = clientBuilder.build();
      client.register(TestFeature3.class);
      client.register(testFeature4);
      client.register(new TestFilter3());
      client.register(testFilter4);
      client.register(new TestMessageBodyReader3());
      client.register(testMessageBodyReader4);  
      client.property("property2", "value2");
      
      clientBuilder.register(TestFeature5.class);
      clientBuilder.register(testFeature6);
      clientBuilder.register(new TestFilter5());
      clientBuilder.register(testFilter6);
      clientBuilder.register(new TestMessageBodyReader5());
      clientBuilder.register(testMessageBodyReader6);  
      clientBuilder.property("property3", "value3");
      
      checkFirstConfiguration(clientBuilder.getConfiguration());
      checkSecondConfiguration(client.getConfiguration());
   }
   
   @Test
   public void testClientToWebTarget()
   {
      ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
      Client client = clientBuilder.build();
      client.register(TestFeature1.class);
      client.register(testFeature2);
      client.register(new TestFilter1());
      client.register(testFilter2);
      client.register(new TestMessageBodyReader1());
      client.register(testMessageBodyReader2);  
      client.property("property1", "value1");
      
      WebTarget target = client.target("http://localhost:8081");
      target.register(TestFeature3.class);
      target.register(testFeature4);
      target.register(new TestFilter3());
      target.register(testFilter4);
      target.register(new TestMessageBodyReader3());
      target.register(testMessageBodyReader4);  
      target.property("property2", "value2");
      
      client.register(TestFeature5.class);
      client.register(testFeature6);
      client.register(new TestFilter5());
      client.register(testFilter6);
      client.register(new TestMessageBodyReader5());
      client.register(testMessageBodyReader6);  
      client.property("property3", "value3");
      
      checkFirstConfiguration(client.getConfiguration());
      checkSecondConfiguration(target.getConfiguration());
   }
   
   @Test
   public void testRuntimeType()
   {
      ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
      Assert.assertEquals(RuntimeType.CLIENT, clientBuilder.getConfiguration().getRuntimeType());
      Client client = clientBuilder.build();
      Assert.assertEquals(RuntimeType.CLIENT, client.getConfiguration().getRuntimeType());
      WebTarget target = client.target("http://localhost:8081");
      Assert.assertEquals(RuntimeType.CLIENT, target.getConfiguration().getRuntimeType());  
   }

   private void checkFirstConfiguration(Configuration config)
   {
      Set<Class<?>> classes = config.getClasses();
      Assert.assertTrue( classes.contains(TestFeature1.class));
      Assert.assertFalse(classes.contains(TestFeature3.class));
      Assert.assertTrue( classes.contains(TestFeature5.class));
      Assert.assertFalse(classes.contains(TestFilter3.class));
      Assert.assertFalse(classes.contains(TestFilter4.class));
      Assert.assertFalse(classes.contains(TestMessageBodyReader3.class));
      Assert.assertFalse(classes.contains(TestMessageBodyReader4.class));
      
      Assert.assertTrue( config.isEnabled(TestFeature1.class));
      Assert.assertTrue( config.isEnabled(TestFeature2.class));
      Assert.assertFalse(config.isEnabled(TestFeature3.class));
      Assert.assertFalse(config.isEnabled(TestFeature4.class));
      Assert.assertTrue( config.isEnabled(TestFeature5.class));
      Assert.assertTrue( config.isEnabled(TestFeature6.class));
      
      Assert.assertTrue( config.isRegistered(TestFeature1.class));
      Assert.assertTrue( config.isRegistered(TestFeature2.class));
      Assert.assertFalse(config.isRegistered(TestFeature3.class));
      Assert.assertFalse(config.isRegistered(TestFeature4.class));
      Assert.assertTrue( config.isRegistered(TestFeature5.class));
      Assert.assertTrue( config.isRegistered(TestFeature6.class));
      Assert.assertTrue( config.isRegistered(TestFilter1.class));
      Assert.assertTrue( config.isRegistered(TestFilter2.class));
      Assert.assertFalse(config.isRegistered(TestFilter3.class));
      Assert.assertFalse(config.isRegistered(TestFilter4.class));
      Assert.assertTrue( config.isRegistered(TestFilter5.class));
      Assert.assertTrue( config.isRegistered(TestFilter6.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader1.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader2.class));
      Assert.assertFalse(config.isRegistered(TestMessageBodyReader3.class));
      Assert.assertFalse(config.isRegistered(TestMessageBodyReader4.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader5.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader6.class));
      
      Set<Object> instances = config.getInstances();
      Assert.assertTrue( instances.contains(testFeature2));
      Assert.assertFalse(instances.contains(testFeature4));
      Assert.assertTrue( instances.contains(testFeature6));
      Assert.assertTrue( instances.contains(testFilter2));
      Assert.assertFalse(instances.contains(testFilter4));
      Assert.assertTrue( instances.contains(testFilter6));
      Assert.assertTrue( instances.contains(testMessageBodyReader2));
      Assert.assertFalse(instances.contains(testMessageBodyReader4));
      Assert.assertTrue( instances.contains(testMessageBodyReader6));
      
      Assert.assertTrue( config.isEnabled(testFeature2));
      Assert.assertFalse(config.isEnabled(testFeature4));
      Assert.assertTrue( config.isEnabled(testFeature6));
      
      Assert.assertTrue( config.isRegistered(testFeature2));
      Assert.assertFalse(config.isRegistered(testFeature4));
      Assert.assertTrue( config.isRegistered(testFeature6));
      Assert.assertTrue( config.isRegistered(testFilter2));
      Assert.assertFalse(config.isRegistered(testFilter4));
      Assert.assertTrue( config.isRegistered(testFilter6));
      Assert.assertTrue(config.isRegistered(testMessageBodyReader2));
      Assert.assertFalse(config.isRegistered(testMessageBodyReader4));
      Assert.assertTrue(config.isRegistered(testMessageBodyReader6));
      
      Assert.assertEquals(2, config.getProperties().size());
      Assert.assertEquals("value1", config.getProperty("property1"));
      Assert.assertEquals("value3", config.getProperty("property3"));
      
      Assert.assertFalse(config.getContracts(TestFeature1.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature2.class).isEmpty());
      Assert.assertTrue(config.getContracts(TestFeature3.class).isEmpty());
      Assert.assertTrue(config.getContracts(TestFeature4.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature5.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature6.class).isEmpty());
   }
   
   private void checkSecondConfiguration(Configuration config)
   {
      Set<Class<?>> classes = config.getClasses();
      Assert.assertTrue( classes.contains(TestFeature1.class));
      Assert.assertTrue( classes.contains(TestFeature3.class));
      Assert.assertFalse(classes.contains(TestFeature5.class));
      
      Assert.assertTrue( config.isEnabled(TestFeature1.class));
      Assert.assertTrue( config.isEnabled(TestFeature2.class));
      Assert.assertTrue( config.isEnabled(TestFeature3.class));
      Assert.assertTrue( config.isEnabled(TestFeature4.class));
      Assert.assertFalse(config.isEnabled(TestFeature5.class));
      Assert.assertFalse(config.isEnabled(TestFeature6.class));
      
      Assert.assertTrue( config.isRegistered(TestFeature1.class));
      Assert.assertTrue( config.isRegistered(TestFeature2.class));
      Assert.assertTrue( config.isRegistered(TestFeature3.class));
      Assert.assertTrue( config.isRegistered(TestFeature4.class));
      Assert.assertFalse(config.isRegistered(TestFeature5.class));
      Assert.assertFalse(config.isRegistered(TestFeature6.class));
      Assert.assertTrue( config.isRegistered(TestFilter1.class));
      Assert.assertTrue( config.isRegistered(TestFilter2.class));
      Assert.assertTrue( config.isRegistered(TestFilter3.class));
      Assert.assertTrue( config.isRegistered(TestFilter4.class));
      Assert.assertFalse(config.isRegistered(TestFilter5.class));
      Assert.assertFalse(config.isRegistered(TestFilter6.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader1.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader2.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader3.class));
      Assert.assertTrue( config.isRegistered(TestMessageBodyReader4.class));
      Assert.assertFalse(config.isRegistered(TestMessageBodyReader5.class));
      Assert.assertFalse(config.isRegistered(TestMessageBodyReader6.class));
      
      Set<Object> instances = config.getInstances();
      Assert.assertTrue( instances.contains(testFeature2));
      Assert.assertTrue( instances.contains(testFeature4));
      Assert.assertFalse(instances.contains(testFeature6));
      Assert.assertTrue( instances.contains(testFilter2));
      Assert.assertTrue( instances.contains(testFilter4));
      Assert.assertFalse(instances.contains(testFilter6));
      Assert.assertTrue( instances.contains(testMessageBodyReader2));
      Assert.assertTrue( instances.contains(testMessageBodyReader4));
      Assert.assertFalse(instances.contains(testMessageBodyReader6));
      
      Assert.assertTrue( config.isEnabled(testFeature2));
      Assert.assertTrue( config.isEnabled(testFeature4));
      Assert.assertFalse(config.isEnabled(testFeature6));
      
      Assert.assertTrue( config.isRegistered(testFeature2));
      Assert.assertTrue( config.isRegistered(testFeature4));
      Assert.assertFalse(config.isRegistered(testFeature6));
      Assert.assertTrue( config.isRegistered(testFilter2));
      Assert.assertTrue( config.isRegistered(testFilter4));
      Assert.assertFalse(config.isRegistered(testFilter6));
      Assert.assertTrue( config.isRegistered(testMessageBodyReader2));
      Assert.assertTrue( config.isRegistered(testMessageBodyReader4));
      Assert.assertFalse(config.isRegistered(testMessageBodyReader6));
      
      Assert.assertEquals(2, config.getProperties().size());
      Assert.assertEquals("value1", config.getProperty("property1"));
      Assert.assertEquals("value2", config.getProperty("property2"));
      
      Assert.assertFalse(config.getContracts(TestFeature1.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature2.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature3.class).isEmpty());
      Assert.assertFalse(config.getContracts(TestFeature4.class).isEmpty());
      Assert.assertTrue( config.getContracts(TestFeature5.class).isEmpty());
      Assert.assertTrue( config.getContracts(TestFeature6.class).isEmpty());
   }
}