package org.jboss.resteasy.test.interceptors;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PrecedenceTest
{

   private static final Logger LOG = Logger.getLogger(PrecedenceTest.class);

   @Precedence("A")
   public static class A
   {
   }

   @Precedence("B")
   public static class B
   {
   }

   @Precedence("C")
   public static class C
   {
   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   @Precedence("D")
   public static @interface D
   {

   }

   @D
   public static class DClass
   {
   }

   @Precedence("BeforeB")
   public static class BeforeB
   {
   }


   @Precedence("AfterC")
   public static class AfterC
   {
   }

   @Precedence("BeforeA")
   public static class BeforeA
   {
   }

   @Precedence("AfterD")
   public static class AfterD
   {
   }

   @Test
   public void testPrecedence() throws Exception
   {
      InterceptorRegistry registry = new InterceptorRegistry(Object.class, ResteasyProviderFactory.getInstance());
      registry.appendPrecedence("A");
      registry.appendPrecedence("B");
      registry.appendPrecedence("C");
      registry.appendPrecedence("D");
      registry.insertPrecedenceAfter("C", "AfterC");
      registry.insertPrecedenceBefore("B", "BeforeB");
      registry.insertPrecedenceBefore("A", "BeforeA");
      registry.insertPrecedenceAfter("D", "AfterD");

      registry.register(PrecedenceTest.class);
      registry.register(B.class);
      registry.register(DClass.class);
      registry.register(C.class);
      registry.register(A.class);
      registry.register(AfterD.class);
      registry.register(BeforeA.class);
      registry.register(BeforeB.class);
      registry.register(AfterC.class);

      Object[] array = registry.bind(null, null);

      for (Object obj : array) LOG.info(obj.getClass().getName());

      Assert.assertEquals(array[0].getClass(), BeforeA.class);
      Assert.assertEquals(array[1].getClass(), A.class);
      Assert.assertEquals(array[2].getClass(), BeforeB.class);
      Assert.assertEquals(array[3].getClass(), B.class);
      Assert.assertEquals(array[4].getClass(), C.class);
      Assert.assertEquals(array[5].getClass(), AfterC.class);
      Assert.assertEquals(array[6].getClass(), DClass.class);
      Assert.assertEquals(array[7].getClass(), AfterD.class);
      Assert.assertEquals(array[8].getClass(), PrecedenceTest.class);
   }
}
