package org.resteasy.util;

import javax.ws.rs.Path;
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
    * @param clazz
    * @return list of class and intertfaces that have jax-rs annotations
    */
   public static List<Class> getRestfulClasses(Class clazz)
   {
      List<Class> rtn = new ArrayList<Class>();
      if (clazz.isAnnotationPresent(Path.class))
      {
         rtn.add(clazz);
      }
      else
      {
         for (Method method : clazz.getMethods())
         {
            if (method.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(method) != null)
            {
               rtn.add(clazz);
               break;
            }
         }
      }
      // ok, no @Path or @HttpMethods so look in interfaces.
      Class[] intfs = clazz.getInterfaces();
      for (Class intf : intfs)
      {
         List<Class> intfRtn = getRestfulClasses(intf);
         if (intfRtn != null) rtn.addAll(intfRtn);
      }
      if (rtn.size() == 0) return null;
      return rtn;
   }
}
