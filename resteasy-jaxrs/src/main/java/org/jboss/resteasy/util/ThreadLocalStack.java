package org.jboss.resteasy.util;

import java.util.ArrayList;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class ThreadLocalStack<T>
{
   private ThreadLocal<ArrayList<T>> local = new ThreadLocal<ArrayList<T>>();

   public void push(T obj)
   {
      ArrayList<T> stack = local.get();
      if (stack == null)
      {
         stack = new ArrayList<T>();
         local.set(stack);
      }
      stack.add(obj);
   }

   public T get()
   {
      ArrayList<T> stack = local.get();
      if (stack == null || stack.isEmpty()) return null;
      return stack.get(stack.size() - 1);
   }

   public void pop()
   {
      ArrayList<T> stack = local.get();
      if (stack != null && !stack.isEmpty()) stack.remove(stack.size() - 1);
   }
}
