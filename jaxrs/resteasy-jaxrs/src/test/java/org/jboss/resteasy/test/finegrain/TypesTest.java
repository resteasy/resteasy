package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.util.PickConstructor;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TypesTest
{
   interface Bar<Q>
   {
      void bar(Q t);
   }

   interface Foo<R, T> extends Bar<T>
   {
      void foo(R r);

      void bar(Double d);
   }

   public static class FooBar implements Foo<Integer, Float>
   {
      @Override
      public void foo(Integer integer)
      {
      }

      @Override
      public void bar(Float aFloat)
      {
      }

      @Override
      public void bar(Double d)
      {
      }
   }

   public static class Sub extends FooBar {}

   public static abstract class Generic<A, B> implements Foo<A, B>
   {}

   public static class GenericSub extends Generic<Integer, Float>
   {
      @Override
      public void foo(Integer integer)
      {
      }

      @Override
      public void bar(Double d)
      {
      }

      @Override
      public void bar(Float t)
      {
      }
   }

   public interface AnotherBar
   {
      void bar(int i);
   }

   public interface AnotherFoo<T> extends AnotherBar
   {
      void foo(T t);
   }

   public class AnotherFooBar implements AnotherFoo<Integer>
   {
      @Override
      public void foo(Integer integer)
      {
      }

      @Override
      public void bar(int i)
      {
      }
   }


   @Test
   public void testInterfaceGenericTypeDiscovery() throws Exception
   {
      Type[] types = Types.findParameterizedTypes(FooBar.class, Bar.class);
      for (Type t : types)
      {
         System.out.println(t);
      }

      Assert.assertEquals(types[0], Float.class);

      types = Types.findParameterizedTypes(Sub.class, Bar.class);
      for (Type t : types)
      {
         System.out.println(t);
      }

      Assert.assertEquals(types[0], Float.class);

      types = Types.findParameterizedTypes(GenericSub.class, Bar.class);
      for (Type t : types)
      {
         System.out.println(t);
      }

      Assert.assertEquals(types[0], Float.class);

      types = Types.findParameterizedTypes(AnotherFooBar.class, AnotherBar.class);
      Assert.assertEquals(0, types.length);
   }

   @Test
   public void testFindImplementedMethod() throws Exception
   {
      Class bar = FooBar.class.getInterfaces()[0].getInterfaces()[0];
      Method barMethod = null;
      for (Method m : bar.getMethods())
      {
         if (!m.isSynthetic() && m.getName().equals("bar")) barMethod = m;
      }

      Method implented = Types.getImplementingMethod(FooBar.class, barMethod);

      Method actual = null;
      for (Method m : FooBar.class.getMethods())
      {
         if (!m.isSynthetic() && m.getName().equals("bar"))
         {
            if (m.getParameterTypes()[0].equals(Float.class)) actual = m;
         }
      }

      Assert.assertEquals(implented, actual);

      System.out.println("Implemented:");
      System.out.println(implented);
      System.out.println("Actual");
      System.out.println(actual);
   }

   static class ClassBar<Q>
   {
      void bar(Q t)
      {

      }
   }

   abstract static class ClassFoo<R, T> extends ClassBar<T>
   {
      void foo(R r)
      {

      }

      void bar(Double d)
      {

      }
   }

   public static class ClassFooBar extends ClassFoo<Integer, Float>
   {
      @Override
      public void foo(Integer integer)
      {
      }

      @Override
      public void bar(Float aFloat)
      {
      }

      @Override
      public void bar(Double d)
      {
      }
   }

   public static class ClassSub extends ClassFooBar {}


   @Test
   public void testClassGenericTypeDiscovery() throws Exception
   {
      Type[] types = Types.findParameterizedTypes(ClassFooBar.class, ClassBar.class);
      for (Type t : types)
      {
         System.out.println(t);
      }

      Assert.assertEquals(types[0], Float.class);

      types = Types.findParameterizedTypes(ClassSub.class, ClassBar.class);
      for (Type t : types)
      {
         System.out.println(t);
      }

      Assert.assertEquals(types[0], Float.class);

   }

   @Test
   public void testPickConstructor()
   {
      Feature feature = new Feature() {
         @Override
         public boolean configure(FeatureContext context)
         {
            return false;
         }
      };

      Constructor constructor = PickConstructor.pickSingletonConstructor(feature.getClass());
      System.out.println(constructor);




   }




}
