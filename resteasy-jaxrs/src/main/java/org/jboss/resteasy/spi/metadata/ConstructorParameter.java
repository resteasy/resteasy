package org.jboss.resteasy.spi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructorParameter extends Parameter
{
   protected Annotation[] annotations = {};
   protected ResourceConstructor constructor;

   protected ConstructorParameter(ResourceConstructor constructor, String name, Class<?> type, Type genericType, Annotation[] annotations)
   {
      super(constructor.getResourceClass(), type, genericType);
      this.annotations = annotations;
      this.constructor = constructor;
      this.paramName = name;
   }

   @Override
   public AccessibleObject getAccessibleObject()
   {
      return constructor.getConstructor();
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }
}
