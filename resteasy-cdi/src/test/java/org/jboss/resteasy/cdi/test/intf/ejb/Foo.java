package org.jboss.resteasy.cdi.test.intf.ejb;

public class Foo implements FooLocal, FooLocal2, FooLocal3
{

   public void foo1()
   {
   }

   public void foo2()
   {
   }

   public String foo3()
   {
      return "foo";
   }
}
