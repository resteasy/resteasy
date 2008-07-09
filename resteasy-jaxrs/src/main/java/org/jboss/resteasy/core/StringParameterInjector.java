package org.jboss.resteasy.core;

import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.StringToPrimitive;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringParameterInjector
{
   protected Class type;
   protected Class baseType;
   protected Constructor constructor;
   protected Method valueOf;
   protected String defaultValue;
   protected String paramName;
   protected String paramType;
   protected boolean isList;
   protected AccessibleObject target;

   public StringParameterInjector()
   {

   }

   public StringParameterInjector(Class type, Type genericType, String paramName, String paramType, String defaultValue, AccessibleObject target)
   {
      initialize(type, genericType, paramName, paramType, defaultValue, target);
   }

   protected void initialize(Class type, Type genericType, String paramName, String paramType, String defaultValue, AccessibleObject target)
   {
      this.type = type;
      this.paramName = paramName;
      this.paramType = paramType;
      this.defaultValue = defaultValue;
      this.target = target;

      baseType = type;
      if (type.isArray()) baseType = type.getComponentType();
      if (List.class.isAssignableFrom(type))
      {
         if (genericType instanceof ParameterizedType)
         {
            ParameterizedType zType = (ParameterizedType) genericType;
            baseType = (Class) zType.getActualTypeArguments()[0];
         }
         else
         {
            baseType = String.class;
         }
         isList = true;
      }

      if (!baseType.isPrimitive())
      {
         try
         {
            constructor = baseType.getConstructor(String.class);
         }
         catch (NoSuchMethodException ignored)
         {

         }
         if (constructor == null)
         {
            try
            {
               valueOf = baseType.getDeclaredMethod("valueOf", String.class);
            }
            catch (NoSuchMethodException e)
            {
               throw new RuntimeException("Unable to find a constructor that takes a String param or a valueOf() method for " + getParamSignature() + " on " + target + " for basetype: " + baseType.getName());
            }

         }
      }
   }

   public String getParamSignature()
   {
      return paramType + "(\"" + paramName + "\")";
   }

   public Object extractValues(List<String> values)
   {
      if (values == null && (type.isArray() || isList) && defaultValue != null)
      {
         values = new ArrayList<String>(1);
         values.add(defaultValue);
      }
      if (type.isArray())
      {
         if (values == null) return null;
         Object vals = Array.newInstance(type.getComponentType(), values.size());
         for (int i = 0; i < values.size(); i++) Array.set(vals, i, extractValue(values.get(i)));
         return vals;
      }
      else if (isList)
      {
         if (values == null) return null;
         ArrayList list = new ArrayList();
         for (String str : values)
         {
            list.add(extractValue(str));
         }
         return list;
      }
      else
      {
         if (values == null) return extractValue(null);
         if (values.size() == 0) return extractValue(null);
         return extractValue(values.get(0));
      }

   }

   public Object extractValue(String strVal)
   {
      if (strVal == null)
      {
         if (defaultValue == null)
         {
            //System.out.println("NO DEFAULT VALUE");
            if (!baseType.isPrimitive()) return null;
         }
         else
         {
            strVal = defaultValue;
            //System.out.println("DEFAULT VAULUE: " + strVal);
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
            throw new RuntimeException("Unable to extract parameter from http request for " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
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
            throw new RuntimeException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (InvocationTargetException e)
         {
            throw new Failure("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e.getTargetException(), HttpResponseCodes.SC_NOT_FOUND);
         }
      }
      return null;
   }
}
