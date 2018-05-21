package org.jboss.resteasy.spi.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DefaultResourceConstructor implements ResourceConstructor
{
   protected ResourceClass resourceClass;
   protected Constructor constructor;
   protected ConstructorParameter[] params = {};

   public DefaultResourceConstructor(ResourceClass resourceClass, Constructor constructor)
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

   @Override
   public ResourceClass getResourceClass()
   {
      return resourceClass;
   }

   @Override
   public Constructor getConstructor()
   {
      return constructor;
   }

   @Override
   public ConstructorParameter[] getParams()
   {
      return params;
   }
}
