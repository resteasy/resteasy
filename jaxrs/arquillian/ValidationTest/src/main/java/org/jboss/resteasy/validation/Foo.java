package org.jboss.resteasy.validation;

import java.io.Serializable;

@FooConstraint(min = 1, max = 3)
public class Foo implements Serializable
{
   private static final long serialVersionUID = -1068336400309384949L;
   public String s;

   public Foo(String s)
   {
      this.s = s;
   }

   public String toString()
   {
      return "Foo[" + s + "]";
   }

   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof Foo))
      {
         return false;
      }
      return this.s.equals(Foo.class.cast(o).s);
   }
}

