package org.jboss.resteasy.test.interceptors;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.core.filter.FilterRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.BindingPriority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FilterPrecedenceTest
{
   private final static int BeforeA = 50;
   private final static int A = 100;
   private final static int BeforeB = 150;
   private final static int B = 200;
   private final static int C = 300;
   private final static int AfterC = 350;
   private final static int D = 400;
   private final static int AfterD = 450;


   @BindingPriority(A)
   public static class A
   {
   }

   @BindingPriority(B)
   public static class B
   {
   }

   @BindingPriority(C)
   public static class C
   {
   }

   @BindingPriority(D)
   public static class DClass
   {
   }

   @BindingPriority(BeforeB)
   public static class BeforeB
   {
   }


   @BindingPriority(AfterC)
   public static class AfterC
   {
   }

   @BindingPriority(BeforeA)
   public static class BeforeA
   {
   }

   @BindingPriority(AfterD)
   public static class AfterD
   {
   }

   @Test
   public void testPrecedence() throws Exception
   {
      FilterRegistry registry = new FilterRegistry(Object.class, ResteasyProviderFactory.getInstance());

      registry.register(FilterPrecedenceTest.class);
      registry.register(B.class);
      registry.register(DClass.class);
      registry.register(C.class);
      registry.register(A.class);
      registry.register(AfterD.class);
      registry.register(BeforeA.class);
      registry.register(BeforeB.class);
      registry.register(AfterC.class);

      Object[] array = registry.bind(null, null);

      for (Object obj : array) System.out.println(obj.getClass().getName());

      Assert.assertEquals(array[0].getClass(), BeforeA.class);
      Assert.assertEquals(array[1].getClass(), A.class);
      Assert.assertEquals(array[2].getClass(), BeforeB.class);
      Assert.assertEquals(array[3].getClass(), B.class);
      Assert.assertEquals(array[4].getClass(), C.class);
      Assert.assertEquals(array[5].getClass(), AfterC.class);
      Assert.assertEquals(array[6].getClass(), DClass.class);
      Assert.assertEquals(array[7].getClass(), AfterD.class);
      Assert.assertEquals(array[8].getClass(), FilterPrecedenceTest.class);
   }
}
