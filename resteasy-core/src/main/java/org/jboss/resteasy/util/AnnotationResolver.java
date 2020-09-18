package org.jboss.resteasy.util;

import java.lang.annotation.Annotation;

public class AnnotationResolver
{
   @SuppressWarnings(value = "unchecked")
   public static Class getClassWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
   {
      if (clazz.isAnnotationPresent(annotation))
      {
         return clazz;
      }
      for (Class intf : clazz.getInterfaces())
      {
         if (intf.isAnnotationPresent(annotation))
         {
            return intf;
         }
      }
      Class superClass = clazz.getSuperclass();
      if (superClass != Object.class && superClass != null)
      {
         return getClassWithAnnotation(superClass, annotation);
      }
      return null;

   }
}
