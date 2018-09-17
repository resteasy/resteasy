package org.jboss.resteasy.util;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GetRestful
{
   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz class
    * @return list of class and intertfaces that have jax-rs annotations
    */
   public static Class getRootResourceClass(Class clazz)
   {
      return AnnotationResolver.getClassWithAnnotation(clazz, Path.class);
   }

   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz class
    * @return list of class and interfaces that have jax-rs annotations
    */
   public static boolean isSubResourceClass(Class clazz)
   {
      // check class & superclasses for JAX-RS annotations
      for (Class<?> actualClass = clazz; isTopObject(actualClass); actualClass = actualClass.getSuperclass())
      {
         if (hasJAXRSAnnotations(actualClass))
            return true;
         // ok, no @Path or @HttpMethods so look in interfaces.
         for (Class intf : actualClass.getInterfaces())
         {
            if (hasJAXRSAnnotations(intf))
               return true;
         }
      }

      return false;
   }
   
   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz class
    * @return list of class and interfaces that have jax-rs annotations
    */
   public static Class getSubResourceClass(Class clazz)
   {
      // check class & superclasses for JAX-RS annotations
      for (Class<?> actualClass = clazz; isTopObject(actualClass); actualClass = actualClass.getSuperclass())
      {
         if (hasJAXRSAnnotations(actualClass))
            return actualClass;
      }

      // ok, no @Path or @HttpMethods so look in interfaces.
      for (Class intf : clazz.getInterfaces())
      {
         if (hasJAXRSAnnotations(intf))
            return intf;
      }
      return null;
   }

   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz class
    * @return list of class and interfaces that have jax-rs annotations
    */
   public static Class<?>[] getSubResourceClasses(Class<?> clazz)
   {
       List<Class<?>> classes = new ArrayList<Class<?>>();
       // check class & superclasses for JAX-RS annotations
       for (Class<?> actualClass = clazz; isTopObject(actualClass); actualClass = actualClass.getSuperclass()) {
           if (hasJAXRSAnnotations(actualClass))
              return new Class<?>[]{actualClass};
       }

       // ok, no @Path or @HttpMethods so look in interfaces.
       for (Class<?> intf : clazz.getInterfaces()) {
           if (hasJAXRSAnnotations(intf))
               classes.add(intf);
       }
       return classes.toArray(new Class<?>[classes.size()]);
   }

   private static boolean isTopObject(Class<?> actualClass)
   {
      return actualClass != null && actualClass != Object.class;
   }

   private static boolean hasJAXRSAnnotations(Class<?> c)
   {
      if (c.isAnnotationPresent(Path.class))
      {
         return true;
      }
      for (Method method : c.isInterface() ? c.getMethods() : c.getDeclaredMethods())
      {
         if (method.isAnnotationPresent(Path.class))
         {
            return true;
         }
         for (Annotation ann : method.getAnnotations())
         {
            if (ann.annotationType().isAnnotationPresent(HttpMethod.class))
            {
               return true;
            }
         }
      }
      return false;
   }

   public static boolean isRootResource(Class clazz)
   {
      return getRootResourceClass(clazz) != null;
   }
}
