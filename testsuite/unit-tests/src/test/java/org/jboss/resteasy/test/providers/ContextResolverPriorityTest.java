package org.jboss.resteasy.test.providers;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

public class ContextResolverPriorityTest extends ResteasyProviderFactory {

   public static class Foo {}

   @Priority(9999)
   public static class TestContextResolver9999 implements ContextResolver<Foo>{

      @Override
      public Foo getContext(Class<?> type) {
         return null;
      }
   }

   public static class TestContextResolver5000 implements ContextResolver<Foo>{

      @Override
      public Foo getContext(Class<?> type) {
         return null;
      }
   }

   @Priority(1000)
   public static class TestContextResolver1000 implements ContextResolver<Foo>{

      @Override
      public Foo getContext(Class<?> type) {
         return null;
      }
   }

   public static class TestResteasyProviderFactory extends ResteasyProviderFactory {}

   @SuppressWarnings("rawtypes")
   @Test
   public void testOlderAddContextResolverMethodsObject() {
      ContextResolverPriorityTest factory = new ContextResolverPriorityTest();
      factory.addContextResolver(new TestContextResolver9999());
      factory.addContextResolver(new TestContextResolver5000());
      factory.addContextResolver(new TestContextResolver1000());
      List<ContextResolver> list = factory.getContextResolvers(Foo.class, MediaType.WILDCARD_TYPE);
      Assert.assertEquals(3, list.size());
      Iterator<ContextResolver> it = list.iterator();
      Assert.assertTrue(TestContextResolver1000.class.equals(it.next().getClass()));
      Assert.assertTrue(TestContextResolver5000.class.equals(it.next().getClass()));
      Assert.assertTrue(TestContextResolver9999.class.equals(it.next().getClass()));
   }

   @SuppressWarnings("rawtypes")
   @Test
   public void testOlderAddContextResolverMethodsClass() {
      ContextResolverPriorityTest factory = new ContextResolverPriorityTest();
      factory.addContextResolver(TestContextResolver9999.class, false);
      factory.addContextResolver(TestContextResolver5000.class, false);
      factory.addContextResolver(TestContextResolver1000.class, false);
      List<ContextResolver> list = factory.getContextResolvers(Foo.class, MediaType.WILDCARD_TYPE);
      Assert.assertEquals(3, list.size());
      Iterator<ContextResolver> it = list.iterator();
      Assert.assertTrue(TestContextResolver1000.class.equals(it.next().getClass()));
      Assert.assertTrue(TestContextResolver5000.class.equals(it.next().getClass()));
      Assert.assertTrue(TestContextResolver9999.class.equals(it.next().getClass()));
   }
}
