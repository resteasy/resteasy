package org.jboss.resteasy.plugins.providers.jackson;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;

public class ResteasyObjectWriterInjector
{
   private static Map<ClassLoader, ObjectWriterModifier> tcclMap = new WeakHashMap<ClassLoader, ObjectWriterModifier>();

   //optimization
   private static final AtomicBoolean hasBeenSet = new AtomicBoolean(false);
   
   private ResteasyObjectWriterInjector() {
   }

   public static void set(ClassLoader cl, ObjectWriterModifier mod)
   {
      if (cl == null) {
         throw new IllegalArgumentException("Null classloader");
      }
      hasBeenSet.set(true);
      synchronized (tcclMap)
      {
         ObjectWriterModifier previous = tcclMap.put(cl, mod);
         if (previous != null && previous != mod) {
            tcclMap.put(cl, mod);
            throw new IllegalArgumentException("A different ObjectWriterModifier is already set for the specified classloader");
         }
      }
   }

   public static ObjectWriterModifier get(ClassLoader cl)
   {
      if (hasBeenSet.get()) {
         synchronized (tcclMap) {
            return tcclMap.get(cl);
         }
      } else {
         return null;
      }
   }
}
