package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.util.Collection;
import java.lang.reflect.Array;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractCollectionProcessor<T>
{
   protected String paramName;

   public AbstractCollectionProcessor(final String paramName)
   {
      this.paramName = paramName;
   }

   protected abstract T apply(T target, Object object);

   protected abstract T apply(T target, Object[] objects);

   public T buildIt(T target, Object object)
   {
      if (object == null) return target;
      if (object instanceof Collection)
      {
         target = apply(target,  ((Collection<?>) object).toArray());
      }
      else if (object.getClass().isArray())
      {
         Object[] arr = convertToObjectsArray(object);
         target = apply(target, arr);
      }
      else
      {
         target = apply(target, object);
      }
      return target;
   }

   private static Object[] convertToObjectsArray(Object array) {
      if(array instanceof Object[])
         return (Object[]) array;

      int length = Array.getLength(array);

      Object[] objects = new Object[length];
      for (int i = 0; i < length; i++) {
          objects[i] = Array.get(array, i);
      }

      return objects;
   }
}
