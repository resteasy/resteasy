package org.jboss.resteasy.spi.metadata;

import org.jboss.resteasy.util.Types;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DefaultResourceLocator implements ResourceLocator
{
   protected ResourceClass resourceClass;
   protected Class<?> returnType;
   protected Type genericReturnType;
   protected Method method;
   protected Method annotatedMethod;
   protected MethodParameter[] params = {};
   protected String fullpath;
   protected String path;

   public DefaultResourceLocator(ResourceClass resourceClass, Method method, Method annotatedMethod)
   {
      this.resourceClass = resourceClass;
      this.annotatedMethod = annotatedMethod;
      this.method = method;
      // we initialize generic types based on the method of the resource class rather than the Method that is actually
      // annotated.  This is so we have the appropriate generic type information.
      this.genericReturnType = Types.resolveTypeVariables(resourceClass.getClazz(), method.getGenericReturnType());
      this.returnType = Types.getRawType(genericReturnType);
      this.params = new MethodParameter[method.getParameterTypes().length];
      Parameter[] reflectionParameters = method.getParameters();
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         this.params[i] = new MethodParameter(this, reflectionParameters[i].getName(), method.getParameterTypes()[i], method.getGenericParameterTypes()[i], annotatedMethod.getParameterAnnotations()[i]);
      }
   }

   @Override
   public ResourceClass getResourceClass()
   {
      return resourceClass;
   }

   @Override
   public Class<?> getReturnType()
   {
      return returnType;
   }

   @Override
   public Type getGenericReturnType()
   {
      return genericReturnType;
   }

   @Override
   public Method getMethod()
   {
      return method;
   }

   @Override
   public Method getAnnotatedMethod()
   {
      return annotatedMethod;
   }

   @Override
   public MethodParameter[] getParams()
   {
      return params;
   }

   @Override
   public String getFullpath()
   {
      return fullpath;
   }

   @Override
   public String getPath()
   {
      return path;
   }

   @Override
   public String toString() {
      return method.toString();
   }
}
