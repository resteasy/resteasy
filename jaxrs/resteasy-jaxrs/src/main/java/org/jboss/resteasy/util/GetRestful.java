package org.jboss.resteasy.util;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GetRestful
{
   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz
    * @return list of class and intertfaces that have jax-rs annotations
    */
   public static Class getRootResourceClass(Class clazz)
   {
      return AnnotationResolver.getClassWithAnnotation(clazz, Path.class);
   }

   /**
    * Given a class, search itself and implemented interfaces for jax-rs annotations.
    *
    * @param clazz
    * @return list of class and intertfaces that have jax-rs annotations
    */
   public static Class getSubResourceClass(Class clazz)
   {
      if (clazz.isAnnotationPresent(Path.class))
      {
         return clazz;
      }
      for (Method method : clazz.getMethods())
      {
         if (method.isAnnotationPresent(Path.class)) return clazz;
         for (Annotation ann : method.getAnnotations())
         {
            if (ann.annotationType().isAnnotationPresent(HttpMethod.class)) return clazz;
         }
      }
      // ok, no @Path or @HttpMethods so look in interfaces.
      Class[] intfs = clazz.getInterfaces();
      for (Class intf : intfs)
      {
         if (intf.isAnnotationPresent(Path.class))
         {
            return intf;
         }
         for (Method method : intf.getMethods())
         {
            if (method.isAnnotationPresent(Path.class)) return intf;
            for (Annotation ann : method.getAnnotations())
            {
               if (ann.annotationType().isAnnotationPresent(HttpMethod.class)) return intf;
            }
         }
      }
      return null;
   }

   public static boolean isRootResource(Class clazz)
   {
      return getRootResourceClass(clazz) != null;
   }
}
