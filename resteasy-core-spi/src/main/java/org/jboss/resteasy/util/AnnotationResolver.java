package org.jboss.resteasy.util;

import org.jboss.resteasy.spi.metadata.ResourceClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationResolver
{
   private static AnnotationResolver instance = new AnnotationResolver();

   public static AnnotationResolver getInstance() {
      return instance;
   }

   public static void setInstance(AnnotationResolver resolver)
   {
      instance = resolver;
   }

   protected AnnotationResolver() {}

   @SuppressWarnings(value = "unchecked")
   public <T extends Annotation> Class getClassWithAnnotation(Class<?> clazz, Class<T> annotation)
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

   public <T extends Annotation> T getAnnotationFromClass(Class<T> annotationClass, Class<?> clazz)
   {
      return clazz.getAnnotation(annotationClass);
   }

   public <T extends Annotation> T getAnnotationFromResourceMethod(Class<T> annotationClass, Method method, ResourceClass resourceClass)
   {
      T annotation = method.getAnnotation(annotationClass);
      if (annotation == null) annotation = resourceClass.getClazz().getAnnotation(annotationClass);
      if (annotation == null) annotation = method.getDeclaringClass().getAnnotation(annotationClass);

      return annotation;
   }

   public <T extends Annotation> T getAnnotationFromMethod(Class<T> annotationClass, Method method)
   {
      return method.getAnnotation(annotationClass);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass, Class clazz) {
      return getAnnotationFromClass(annotationClass, clazz) != null;
   }

}
