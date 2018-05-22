package org.jboss.resteasy.spi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FieldParameter extends Parameter
{
   protected Field field;

   protected FieldParameter(ResourceClass declaredClass, Field field)
   {
      super(declaredClass, field.getType(), field.getGenericType());
      this.field = field;
      this.paramName = field.getName();
   }

   @Override
   public AccessibleObject getAccessibleObject()
   {
      return field;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return field.getAnnotations();
   }

   public Field getField()
   {
      return field;
   }
}
