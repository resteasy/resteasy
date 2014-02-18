package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContextResolverTest
{
   @BeforeClass
   public static void before()
   {
      ResteasyProviderFactory.setInstance(new ResteasyProviderFactory());
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      factory.addContextResolver(Resolver1.class);
      factory.addContextResolver(Resolver2.class);
      factory.addContextResolver(Resolver3.class);
      factory.addContextResolver(Resolver4.class);
      factory.addContextResolver(Resolver5.class);
      factory.addContextResolver(Resolver6.class);
   }

   @Provider
   public static class Resolver1 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(int.class)) return "1";
         return null;
      }
   }

   @Provider
   @Produces("text/plain")
   public static class Resolver2 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(int.class)) return "2";
         return null;
      }
   }

   @Provider
   @Produces("text/*")
   public static class Resolver3 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(int.class)) return "3";
         return null;
      }
   }

   @Provider
   public static class Resolver4 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(float.class)) return "4";
         return null;
      }
   }

   @Provider
   @Produces("text/plain")
   public static class Resolver5 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(float.class)) return "5";
         return null;
      }
   }

   @Provider
   @Produces("text/*")
   public static class Resolver6 implements ContextResolver<String>
   {
      public String getContext(Class<?> type)
      {
         if (type.equals(float.class)) return "6";
         return null;
      }
   }

   @Test
   public void testContextResolver()
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      ContextResolver<String> resolver = factory.getContextResolver(String.class, MediaType.TEXT_PLAIN_TYPE);
      Assert.assertEquals(resolver.getContext(int.class), "2");
      Assert.assertEquals(resolver.getContext(float.class), "5");
      resolver = factory.getContextResolver(String.class, MediaType.TEXT_XML_TYPE);
      Assert.assertEquals(resolver.getContext(int.class), "3");
      Assert.assertEquals(resolver.getContext(float.class), "6");
      resolver = factory.getContextResolver(String.class, MediaType.APPLICATION_ATOM_XML_TYPE);
      Assert.assertEquals(resolver.getContext(int.class), "1");
      Assert.assertEquals(resolver.getContext(float.class), "4");
      Assert.assertNull(resolver.getContext(double.class));
      Assert.assertNull(factory.getContextResolver(Double.class, MediaType.APPLICATION_ATOM_XML_TYPE));

   }

}
