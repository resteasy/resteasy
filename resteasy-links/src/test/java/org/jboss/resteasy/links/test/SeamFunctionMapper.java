package org.jboss.resteasy.links.test;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves Seam Security EL functions, s:hasRole() and s:hasPermission()
 * by decorating a delegate Unified EL FunctionMapper
 *  
 * @author Shane Bryzak
 */
public class SeamFunctionMapper extends FunctionMapper
{
   private static Map<String,List<Method>> methodCache = new HashMap<String,List<Method>>();
   
   private FunctionMapper functionMapper;
   
   public SeamFunctionMapper(FunctionMapper functionMapper)
   {
      this.functionMapper = functionMapper;
   }
   
   static 
   {
      cacheMethod("hasPermission", SecurityFunctions.class, "hasPermission",
               new Class[] {Object.class, String.class});
      cacheMethod("hasRole", SecurityFunctions.class, "hasRole",
               new Class[] { String.class });      
   }

   @Override 
   public Method resolveFunction(String prefix, String localName) 
   {
      if ( "s".equals(prefix) )
      {
         List<Method> methods = methodCache.get(localName);
         return methods != null ? methods.get(0) : null;
      }
      else if (functionMapper != null)
      {
         return functionMapper.resolveFunction(prefix, localName);
      }
      else
      {
         return null;
      }
   }

    @Override
    public void mapFunction(String prefix, String localName, Method meth) {
        super.mapFunction(prefix, localName, meth);
    }

   /* @Override
   public Method resolveFunction(String prefix, String localName, int paramCount) 
   {
      if ( "s".equals(prefix) )
      {
         List<Method> methods = methodCache.get(localName);
         if (methods != null)
         {
            for (Method m : methods)
            {
               if (m.getParameterTypes().length == paramCount) return m;
            }
         }

         return null;
      }
      else if (functionMapper != null)
      {
         return functionMapper.resolveFunction(prefix, localName);
      }
      else
      {
         return null;
      }
   }    */
   
   private static void cacheMethod(String localName, Class<?> cls, String name, Class<?>[] params)
   {
      try
      {
         Method m = cls.getMethod(name, params);

         List<Method> methods;
         if (methodCache.containsKey(localName))
         {
            methods = methodCache.get(localName);
         }
         else
         {
            methods = new ArrayList<Method>();
            methodCache.put(localName, methods);
         }
         
         methods.add(m);         
      }
      catch (NoSuchMethodException ex)
      {
         System.err.println(String.format("Method %s.%s could not be cached", cls.getName(), name));
      }
   }
   
}
