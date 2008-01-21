package org.resteasy;

import org.resteasy.util.StringToPrimitive;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class StringParameterExtractor implements ParameterExtractor
{
   protected Class type;
   protected Class baseType;
   protected Constructor constructor;
   protected Method method;
   protected Method valueOf;
   protected String defaultValue;
   protected String paramName;
   protected String paramType;

   public StringParameterExtractor(Class type, Method method, String paramName, String paramType, String defaultValue)
   {
      this.type = type;
      this.method = method;
      this.paramName = paramName;
      this.paramType = paramType;

      baseType = type;
      if (type.isArray()) baseType = type.getComponentType();

      if (!baseType.isPrimitive())
      {
         try
         {
            constructor = type.getConstructor(String.class);
         }
         catch (NoSuchMethodException ignored)
         {

         }
         if (constructor == null)
         {
            try
            {
               valueOf = type.getDeclaredMethod("valueOf", String.class);
            }
            catch (NoSuchMethodException e)
            {
               throw new RuntimeException("Unable to find a constructor that takes a String param or a valueOf() method for " + getParamSignature() + " on " + method);
            }

         }
      }
   }

   protected String getParamSignature()
   {
      return paramType + "(\"" + paramName + "\")";
   }

   protected Object extractValues(List<String> values)
   {
      if (type.isArray())
      {
         if (values == null)
         {
            Object[] vals = (Object[]) Array.newInstance(type.getComponentType(), 1);
            vals[0] = extractValue(null);
            return vals;
         }
         Object[] vals = (Object[]) Array.newInstance(type.getComponentType(), values.size());
         for (int i = 0; i < vals.length; i++) vals[i] = extractValue(values.get(i));
         return vals;
      }
      else
      {
         return extractValue(values.get(0));
      }

   }

   protected Object extractValue(String strVal)
   {
      if (strVal == null)
      {
         if (defaultValue == null)
         {
            if (baseType.isPrimitive()) strVal = "0";
            else return null;
         }
         else
         {
            strVal = defaultValue;
         }
      }
      if (baseType.isPrimitive()) return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
      if (constructor != null)
      {
         try
         {
            return constructor.newInstance(strVal);
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException("Unable to extract parameter from http request for " + getParamSignature() + " value is '" + strVal + "'" + " for method " + method, e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException("Unable to extra parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for method " + method, e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException("Unable to extra parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for method " + method, e.getTargetException());
         }
      }
      if (valueOf != null)
      {
         try
         {
            return valueOf.invoke(null, strVal);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException("Unable to extra parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for method " + method, e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException("Unable to extra parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for method " + method, e.getTargetException());
         }
      }
      return null;
   }
}
