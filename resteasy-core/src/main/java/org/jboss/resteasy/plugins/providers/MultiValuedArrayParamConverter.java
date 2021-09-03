package org.jboss.resteasy.plugins.providers;


import java.lang.reflect.Array;
import java.util.Arrays;

import jakarta.ws.rs.ext.ParamConverter;


import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
public class MultiValuedArrayParamConverter extends MultiValuedAbstractParamConverter implements ParamConverter<Object>
{
   private Class<?> rawType;

   public MultiValuedArrayParamConverter(final StringParameterInjector stringParameterInjector, final String separator, final Class<?> rawType)
   {
      super(stringParameterInjector, separator);
      this.rawType = rawType;
   }

   @Override
   public String toString(Object value)
   {
      if (value == null)
      {
         return null;
      }
      if (!value.getClass().isArray())
      {
         throw new RuntimeException(Messages.MESSAGES.expectedArray(value.getClass().getName()));
      }
      Object[] values = new Object[Array.getLength(value)];
      if (values.length == 0)
      {
         return "";
      }
      if (rawType.getComponentType().isPrimitive())
      {
         copyPrimitiveArray(values, value);
      }
      else
      {
         values = (Object[]) value;
      }
      return stringify(Arrays.asList(values));
   }

   @Override
   public Object fromString(String param)
   {
      String[] elements = param.split(separator);
      Object array = Array.newInstance(rawType.getComponentType(), elements.length);
      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            Array.set(array, i, stringParameterInjector.extractValue(elements[i]));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(Messages.MESSAGES.errorCreatingArray(param));
      }
      return array;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private void copyPrimitiveArray(Object[] objectArray, Object primitiveArray)
   {
      int primitiveArrayLength= Array.getLength(primitiveArray);
      for (int i = 0; i < primitiveArrayLength; i++)
      {
         objectArray[i] = Array.get(primitiveArray, i);
      }
   }
}
