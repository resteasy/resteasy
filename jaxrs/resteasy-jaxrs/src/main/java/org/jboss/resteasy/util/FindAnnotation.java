package org.jboss.resteasy.util;

import java.lang.annotation.Annotation;
import java.util.LinkedList;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FindAnnotation
{
   public static <T> T findAnnotation(Annotation[] searchList, Class<T> annotation)
   {
      for (Annotation ann : searchList)
      {
         if (ann.annotationType().equals(annotation)) return (T) ann;
      }
      return null;
   }

   private final static Class<? extends Annotation>[] JAXRS_ANNOTATIONS =
      (Class<? extends Annotation>[]) new Class[] { 
         QueryParam.class,
         HeaderParam.class,
         CookieParam.class,
         PathParam.class,
         MatrixParam.class,
         Context.class
   };
   
   private final static Class[] findJaxRSAnnotations_TYPE = 
      new Class[]{};
   
   public static Class<? extends Annotation>[] findJaxRSAnnotations(Annotation[] searchList)
   {
  
      LinkedList<Class<? extends Annotation>> result = new LinkedList<Class<? extends Annotation>>();

      for ( Class<? extends Annotation> clazz : JAXRS_ANNOTATIONS )
      {
         
         if ( findAnnotation(searchList, clazz) != null )
            result.add(clazz);
         
      }

      return result.toArray(findJaxRSAnnotations_TYPE);
      
   }
   
}
