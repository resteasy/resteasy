package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.util.Collection;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractCollectionProcessor<T>
{
   protected String paramName;

   public AbstractCollectionProcessor(String paramName)
   {
      this.paramName = paramName;
   }

   protected abstract T apply(T target, Object object);

   public T buildIt(T target, Object object)
   {
      if (object == null) return target;
      if (object instanceof Collection)
      {
         for (Object obj : (Collection) object)
         {
            target = apply(target, obj);
         }
      }
      else if (object.getClass().isArray())
      {
         if (object.getClass().getComponentType().isPrimitive())
         {
            Class componentType = object.getClass().getComponentType();
            if (componentType.equals(boolean.class))
            {
               for (Boolean bool : (boolean[]) object) target = apply(target, bool.toString());
            }
            else if (componentType.equals(byte.class))
            {
               for (Byte val : (byte[]) object) target = apply(target, val.toString());
            }
            else if (componentType.equals(short.class))
            {
               for (Short val : (short[]) object) target = apply(target, val.toString());
            }
            else if (componentType.equals(int.class))
            {
               for (Integer val : (int[]) object) target = apply(target, val.toString());
            }
            else if (componentType.equals(long.class))
            {
               for (Long val : (long[]) object) target = apply(target, val.toString());
            }
            else if (componentType.equals(float.class))
            {
               for (Float val : (float[]) object) target = apply(target, val.toString());
            }
            else if (componentType.equals(double.class))
            {
               for (Double val : (double[]) object) target = apply(target, val.toString());
            }
         }
         else
         {
            Object[] objs = (Object[]) object;
            for (Object obj : objs)
            {
               target = apply(target, obj);

            }
         }
      }
      else
      {
         target = apply(target, object);
      }
      return target;
   }
}
