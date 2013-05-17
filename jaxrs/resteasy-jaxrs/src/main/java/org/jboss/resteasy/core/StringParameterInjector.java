package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.StringToPrimitive;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringParameterInjector
{
   protected Class type;
   protected Class baseType;
   protected Type baseGenericType;
   protected Constructor constructor;
   protected Method valueOf;
   protected String defaultValue;
   protected String paramName;
   protected Class paramType;
   protected boolean isCollection;
   protected Class<? extends Collection> collectionType;
   protected AccessibleObject target;
   protected ParamConverter paramConverter;
   protected StringConverter converter;
   protected StringParameterUnmarshaller unmarshaller;
   protected RuntimeDelegate.HeaderDelegate delegate;

   public StringParameterInjector()
   {

   }

   public StringParameterInjector(Class type, Type genericType, String paramName, Class paramType, String defaultValue, AccessibleObject target, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      initialize(type, genericType, paramName, paramType, defaultValue, target, annotations, factory);
   }

   public boolean isCollectionOrArray()
   {
      return isCollection || type.isArray();
   }

   protected void initialize(Class type, Type genericType, String paramName, Class paramType, String defaultValue, AccessibleObject target, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.paramName = paramName;
      this.paramType = paramType;
      this.defaultValue = defaultValue;
      this.target = target;
      baseType = type;
      baseGenericType = genericType;

      if (type.isArray()) baseType = type.getComponentType();
      if (List.class.isAssignableFrom(type))
      {
         isCollection = true;
         collectionType = ArrayList.class;
      }
      else if (SortedSet.class.isAssignableFrom(type))
      {
         isCollection = true;
         collectionType = TreeSet.class;
      }
      else if (Set.class.isAssignableFrom(type))
      {
         isCollection = true;
         collectionType = HashSet.class;
      }
      if (isCollection)
      {
         if (genericType != null && genericType instanceof ParameterizedType)
         {
            ParameterizedType zType = (ParameterizedType) genericType;
            baseType = (Class) zType.getActualTypeArguments()[0];
            baseGenericType = zType.getActualTypeArguments()[0];
         }
         else
         {
            baseType = String.class;
            baseGenericType = null;
         }
      }
      if (!baseType.isPrimitive())
      {
         paramConverter = factory.getParamConverter(baseType, baseGenericType, annotations);
         if (paramConverter != null) return;

         unmarshaller = factory.createStringParameterUnmarshaller(baseType);
         if (unmarshaller != null)
         {
            unmarshaller.setAnnotations(annotations);
            return;
         }

         for (Annotation annotation : annotations)
         {
            StringParameterUnmarshallerBinder binder = annotation.annotationType().getAnnotation(StringParameterUnmarshallerBinder.class);
            if (binder != null)
            {
               try
               {
                  unmarshaller = binder.value().newInstance();
               }
               catch (InstantiationException e)
               {
                  throw new RuntimeException(e.getCause());
               }
               catch (IllegalAccessException e)
               {
                  throw new RuntimeException(e);
               }
               factory.injectProperties(unmarshaller);
               unmarshaller.setAnnotations(annotations);
               return;
            }
         }

         converter = factory.getStringConverter(baseType);
         if (converter != null) return;

         if (paramType.equals(HeaderParam.class))
         {
            delegate = factory.getHeaderDelegate(baseType);
            if (delegate != null) return;
         }


         try
         {
            constructor = baseType.getConstructor(String.class);
            if (!Modifier.isPublic(constructor.getModifiers())) constructor = null;
         }
         catch (NoSuchMethodException ignored)
         {

         }
         if (constructor == null)
         {
            try
            {
               // this is for JAXB generated enums.
               Method fromValue = baseType.getDeclaredMethod("fromValue", String.class);
               if (Modifier.isPublic(fromValue.getModifiers()))
               {
                  for (Annotation ann : baseType.getAnnotations())
                  {
                     if (ann.annotationType().getName().equals("javax.xml.bind.annotation.XmlEnum"))
                     {
                        valueOf = fromValue;
                     }
                  }
               }
            }
            catch (NoSuchMethodException e)
            {
            }
            if (valueOf == null)
            {
               Method fromString = null;

               try
               {
                  fromString = baseType.getDeclaredMethod("fromString", String.class);
                  if (Modifier.isStatic(fromString.getModifiers()) == false) fromString = null;
               }
               catch (NoSuchMethodException ignored)
               {
               }
               try
               {
                  valueOf = baseType.getDeclaredMethod("valueOf", String.class);
                  if (Modifier.isStatic(valueOf.getModifiers()) == false) valueOf = null;
               }
               catch (NoSuchMethodException ignored)
               {
               }
               // If enum use fromString if it exists: as defined in JAX-RS spec
               if (baseType.isEnum())
               {
                  if (fromString != null)
                  {
                     valueOf = fromString;
                  }
               }
               else if (valueOf == null)
               {
                  valueOf = fromString;
               }
               if (valueOf == null)
               {
                  throw new RuntimeException("Unable to find a constructor that takes a String param or a valueOf() or fromString() method for " + getParamSignature() + " on " + target + " for basetype: " + baseType.getName());

               }
            }

         }
      }
   }

   public String getParamSignature()
   {
      return paramType.getName() + "(\"" + paramName + "\")";
   }

   public Object extractValues(List<String> values)
   {
      if (values == null && (type.isArray() || isCollection) && defaultValue != null)
      {
         values = new ArrayList<String>(1);
         values.add(defaultValue);
      }
      else if (values == null)
      {
         values = Collections.emptyList();
      }
      if (type.isArray())
      {
         if (values == null) return null;
         Object vals = Array.newInstance(type.getComponentType(), values.size());
         for (int i = 0; i < values.size(); i++) Array.set(vals, i, extractValue(values.get(i)));
         return vals;
      }
      else if (isCollection)
      {
         if (values == null) return null;
         Collection collection = null;
         try
         {
            collection = collectionType.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         for (String str : values)
         {
            collection.add(extractValue(str));
         }
         return collection;
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
      if (paramConverter != null)
      {
         return paramConverter.fromString(strVal);
      }
      if (converter != null)
      {
         return converter.fromString(strVal);
      }
      else if (unmarshaller != null)
      {
         return unmarshaller.fromString(strVal);
      }
      else if (delegate != null)
      {
         return delegate.fromString(strVal);
      }
      else if (constructor != null)
      {
         try
         {
            return constructor.newInstance(strVal);
         }
         catch (InstantiationException e)
         {
            throwProcessingException("Unable to extract parameter from http request for " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (IllegalAccessException e)
         {
            throwProcessingException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, targetException);
         }
      }
      else if (valueOf != null)
      {
         try
         {
            return valueOf.invoke(null, strVal);
         }
         catch (IllegalAccessException e)
         {
            throwProcessingException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, e);
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException("Unable to extract parameter from http request: " + getParamSignature() + " value is '" + strVal + "'" + " for " + target, targetException);
         }
      }
      return null;
   }

   protected void throwProcessingException(String message, Throwable cause)
   {
      throw new BadRequestException(message, cause);
   }
}
