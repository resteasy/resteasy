package org.jboss.resteasy.spi.metadata;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SetterParameter extends Parameter
{
   protected Method setter;
   protected Method annotatedMethod;

   protected SetterParameter(ResourceClass declaredClass, Method setter, Method annotatedMethod)
   {
      super(declaredClass, setter.getParameterTypes()[0], setter.getGenericParameterTypes()[0]);
      this.setter = setter;
      this.annotatedMethod = annotatedMethod;
      this.paramName = Introspector.decapitalize(setter.getName().substring(3));
   }

   public Method getSetter()
   {
      return setter;
   }

   public Method getAnnotatedMethod()
   {
      return annotatedMethod;
   }

   @Override
   public AccessibleObject getAccessibleObject()
   {
      return setter;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return annotatedMethod.getAnnotations();
   }

}
