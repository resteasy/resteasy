package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.util.Types;

import jakarta.ws.rs.Priorities;

/**
 * Allow us to sort message body implementations that are more specific for their types
 * i.e. MessageBodyWriter&#x3C;Object&#x3E; is less specific than MessageBodyWriter&#x3C;String&#x3E;.
 * <p>
 * This helps out a lot when the desired media type is a wildcard and to weed out all the possible
 * default mappings.
 */
public class SortedKey<T> implements Comparable<SortedKey<T>>, MediaTypeMap.Typed
{
   private final T obj;
   private final boolean isBuiltin;
   private final Class<?> template;
   private final int priority;

   public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final int priority, final boolean isBuiltin)
   {
      this.obj = reader;
      // check the super class for the generic type 1st
      Class<?> t = Types.getTemplateParameterOfInterface(readerClass, intf);
      template = (t != null) ? t : Object.class;
      this.priority = priority;
      this.isBuiltin = isBuiltin;
   }

   public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final boolean isBuiltin)
   {
      this(intf, reader, readerClass, Priorities.USER, isBuiltin);
   }

   public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass)
   {
      this(intf, reader, readerClass, Priorities.USER, false);
   }

   /**
    * Direct populate
    *
    * @param obj
    * @param isBuiltin
    * @param template template class of component type
    * @param priority
    */
   public SortedKey(final T obj, final boolean isBuiltin, final Class<?> template, final int priority) {
      this.obj = obj;
      this.isBuiltin = isBuiltin;
      this.template = template;
      this.priority = priority;
   }

   public int compareTo(SortedKey<T> tMessageBodyKey)
   {
      // Sort user provider before builtins
      if (this == tMessageBodyKey)
         return 0;
      if (isBuiltin == tMessageBodyKey.isBuiltin)
      {
         if (this.priority < tMessageBodyKey.priority)
         {
            return -1;
         }
         if (this.priority == tMessageBodyKey.priority)
         {
            return 0;
         }
         if (this.priority > tMessageBodyKey.priority)
         {
            return 1;
         }
      }
      if (isBuiltin)
         return 1;
      else
         return -1;
   }

   public Class<?> getType()
   {
      return template;
   }

   public T getObj()
   {
      return obj;
   }

   public int getPriority() {
      return priority;
   }
}
