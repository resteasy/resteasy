package org.jboss.resteasy.util;

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
    *
    * @param clazz
    * @return
    */
   public static Constructor pickConstructor(Class clazz)
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
            numParameters = con.getParameterTypes().length;
            pick = con;
         }
      }
      return pick;
   }
}
