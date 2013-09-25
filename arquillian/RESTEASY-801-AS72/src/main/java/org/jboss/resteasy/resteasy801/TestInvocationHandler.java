package org.jboss.resteasy.resteasy801;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class TestInvocationHandler implements InvocationHandler
{
   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      System.out.println("entered proxied subresource");
      System.out.println("method: " + method.getName());
      System.out.println("generic return type: " + method.getGenericReturnType());
      System.out.println("type of return type: " + method.getGenericReturnType().getClass());
      if ("resourceMethod".equals(method.getName())) {
          List<AbstractParent> l = new ArrayList<AbstractParent>();
          Type1 first = new Type1();
          first.setId(1);
          first.setName("MyName");
          l.add(first);
          
          Type2 second = new Type2();
          second.setId(2);
          second.setNote("MyNote");
          l.add(second);
          return l;
      }
      
      if ("resourceMethodOne".equals(method.getName())) {
          Type1 first = new Type1();
          first.setId(1);
          first.setName("MyName");
          return first;
      }
      
      return null;
   }
}
