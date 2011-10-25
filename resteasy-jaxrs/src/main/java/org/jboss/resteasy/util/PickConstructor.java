package org.jboss.resteasy.util;

import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Pick
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PickConstructor
{
   /**
    * Pick best constructor for a provider or resource class
    * <p/>
    * Picks constructor with most parameters.  Will ignore constructors that have parameters with no @Context annotation
    *
    * @param clazz
    * @return
    */
   public static Constructor pickSingletonConstructor(Class clazz)
   {
      Constructor<?>[] constructors = clazz.getConstructors();
      Constructor<?> constructor = null;
      // prefer a no-arg constructor
      int numParameters = 0;
      Constructor pick = null;
      for (Constructor con : constructors)
      {
         if (Modifier.isPublic(con.getModifiers()) == false)
         {
            continue;
         }
         if (con.getParameterTypes().length >= numParameters)
         {
            boolean noContextAnnotation = false;
            if (con.getParameterAnnotations() != null)
            {
               for (Annotation[] ann : con.getParameterAnnotations())
               {
                  if (FindAnnotation.findAnnotation(ann, Context.class) == null)
                  {
                     noContextAnnotation = true;
                  }
               }
            }
            if (noContextAnnotation) continue;
            numParameters = con.getParameterTypes().length;
            pick = con;
         }
      }
      return pick;
   }

   /**
    * Pick best constructor for a provider or resource class
    * <p/>
    * Picks constructor with most parameters.  Will ignore constructors that have parameters with no @Context annotation
    *
    * @param clazz
    * @return
    */
   public static Constructor pickPerRequestConstructor(Class clazz)
   {
      Constructor<?>[] constructors = clazz.getConstructors();
      Constructor<?> constructor = null;
      // prefer a no-arg constructor
      int numParameters = 0;
      Constructor pick = null;
      for (Constructor con : constructors)
      {
         if (Modifier.isPublic(con.getModifiers()) == false)
         {
            continue;
         }
         if (con.getParameterTypes().length >= numParameters)
         {
            boolean noContextAnnotation = false;
            if (con.getParameterAnnotations() != null)
            {
               for (Annotation[] ann : con.getParameterAnnotations())
               {
                  if (FindAnnotation.findJaxRSAnnotations(ann).length == 0)
                  {
                     noContextAnnotation = true;
                  }
               }
            }
            if (noContextAnnotation) continue;
            numParameters = con.getParameterTypes().length;
            pick = con;
         }
      }
      return pick;
   }
}
