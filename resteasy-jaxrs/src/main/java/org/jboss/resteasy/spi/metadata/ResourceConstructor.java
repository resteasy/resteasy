package org.jboss.resteasy.spi.metadata;

import org.jboss.resteasy.util.Types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceConstructor
{
   protected ResourceClass resourceClass;
   protected Constructor constructor;
   protected ConstructorParameter[] params = {};

   public ResourceConstructor(ResourceClass resourceClass, Constructor constructor)
   {
      this.resourceClass = resourceClass;
      this.constructor = constructor;
      if (constructor.getParameterTypes() != null)
      {
         this.params = new ConstructorParameter[constructor.getParameterTypes().length];
         Parameter[] reflectionParameters = constructor.getParameters();
         for (int i = 0; i < constructor.getParameterTypes().length; i++)
         {
            this.params[i] = new ConstructorParameter(this, reflectionParameters[i].getName(), constructor.getParameterTypes()[i], constructor.getGenericParameterTypes()[i], constructor.getParameterAnnotations()[i]);
         }
      }
   }

   public ResourceClass getResourceClass()
   {
      return resourceClass;
   }

   public Constructor getConstructor()
   {
      return constructor;
   }

   public ConstructorParameter[] getParams()
   {
      return params;
   }
}
