package org.jboss.resteasy.spi.metadata;

import org.jboss.resteasy.util.Types;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocator
{
   protected ResourceClass resourceClass;
   protected Class<?> returnType;
   protected Type genericReturnType;
   protected Method method;
   protected Method annotatedMethod;
   protected MethodParameter[] params = {};
   protected String fullpath;
   protected String path;

   public ResourceLocator(ResourceClass resourceClass, Method method, Method annotatedMethod)
   {
      this.resourceClass = resourceClass;
      this.annotatedMethod = annotatedMethod;
      this.method = method;
      // we initialize generic types based on the method of the resource class rather than the Method that is actually
      // annotated.  This is so we have the appropriate generic type information.
      this.genericReturnType = Types.resolveTypeVariables(resourceClass.getClazz(), method.getGenericReturnType());
      this.returnType = Types.getRawType(genericReturnType);
      this.params = new MethodParameter[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         this.params[i] = new MethodParameter(this, method.getParameterTypes()[i], method.getGenericParameterTypes()[i], annotatedMethod.getParameterAnnotations()[i]);
      }
   }

   public ResourceClass getResourceClass()
   {
      return resourceClass;
   }

   public Class<?> getReturnType()
   {
      return returnType;
   }

   public Type getGenericReturnType()
   {
      return genericReturnType;
   }

   public Method getMethod()
   {
      return method;
   }

   public Method getAnnotatedMethod()
   {
      return annotatedMethod;
   }

   public MethodParameter[] getParams()
   {
      return params;
   }

   public String getFullpath()
   {
      return fullpath;
   }

   public String getPath()
   {
      return path;
   }

   @Override
   public String toString() {
      return method.toString();
   }
}
