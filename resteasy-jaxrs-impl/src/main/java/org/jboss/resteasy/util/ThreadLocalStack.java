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
      getStack(true).add(obj);
   }

   private ArrayList<T> getStack(boolean create)
   {
      ArrayList<T> stack = local.get();
      if (stack == null && create)
      {
         stack = new ArrayList<T>();
         local.set(stack);
      }
      return stack;
   }

   public T get()
   {
      ArrayList<T> stack = local.get();
      if (stack == null || stack.isEmpty()) return null;
      return stack.get(stack.size() - 1);
   }

   public T pop()
   {
      ArrayList<T> stack = local.get();
      if (stack == null || stack.isEmpty()) return null;
      return stack.remove(stack.size() - 1);
   }

   public void setLast(T obj)
   {
      ArrayList<T> stack = getStack(true);
      if (stack.isEmpty())
      {
         stack.add(obj);
      }
      else
      {
         stack.set(stack.size() - 1, obj);
      }
   }

   public boolean isEmpty()
   {
      ArrayList<T> stack = getStack(false);
      return stack == null || stack.isEmpty();
   }

   public int size()
   {
      ArrayList<T> stack = getStack(false);
      return stack == null ? 0 : stack.size();
   }

   public void clear()
   {
      local.set(null);
   }
}
