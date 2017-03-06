package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.StringToPrimitive;
import org.jboss.resteasy.util.Types;

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
@SuppressWarnings(value = "unchecked")
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

      if (type.isArray())
      {
    	  baseType = type.getComponentType();
      }
     
      if (!baseType.isPrimitive())
      {
    	  boolean initialized = initialize(annotations, factory);
    	  if(!initialized)
    	  {
	         collectionType = convertParameterTypeToCollectionType();
	         if (collectionType != null)
	         {
	        	 isCollection = true;
		         if (genericType instanceof ParameterizedType)
		         {
		            ParameterizedType zType = (ParameterizedType) baseGenericType;
		            baseType = Types.getRawType(zType.getActualTypeArguments()[0]);
		            baseGenericType = zType.getActualTypeArguments()[0];
		         }
		         else
		         {
		            baseType = String.class;
		            baseGenericType = null;
		         }
		         if(baseType.isPrimitive())
		         {
		        	 return;
		         }
		         initialized = initialize(annotations, factory);
	        }
	        if(!initialized)
	    	{  
	        	throw new RuntimeException(Messages.MESSAGES.unableToFindConstructor(getParamSignature(), target, baseType.getName()));
	    	}
    	}
     }
      
  }
   
   
   private boolean initialize(Annotation[] annotations, ResteasyProviderFactory factory){
	   
	   // First try to find a ParamConverter if any
	   paramConverter = factory.getParamConverter(baseType, baseGenericType, annotations);
       if (paramConverter != null) 
       {
      	 return true;
       }
       
       // Else try to find a StringParameterUnmarshaller if any
       unmarshaller = factory.createStringParameterUnmarshaller(baseType);
       if (unmarshaller != null)
       {
          unmarshaller.setAnnotations(annotations);
          return true;
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
             return true;
          }
       }
       
       // Else try to find a StringConverter if any
       converter = factory.getStringConverter(baseType);
       if (converter != null)
       {
    	   return true;
       }
       
       // Else try to find a RuntimeDelegate.HeaderDelegate if any
       if (paramType.equals(HeaderParam.class))
       {
          delegate = factory.getHeaderDelegate(baseType);
          if (delegate != null)
          {
        	  return true;
          }
       }
       
       // Else try to find a public Constructor that accepts a single String argument if any
       try
       {
          constructor = baseType.getConstructor(String.class);
          if (!Modifier.isPublic(constructor.getModifiers())) 
          {
        	  constructor = null;
          }
          else
          {
        	  return true;
          }
       }
       catch (NoSuchMethodException ignored)
       {

       }
       
	  // Else try to find a public fromValue (JAXB enum) or valueOf or fromString method that accepts a single String argument if any.
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
      }
      
      return valueOf != null;
   }
   
	private Class<? extends Collection> convertParameterTypeToCollectionType() {
		if (List.class.equals(type)) {
			return ArrayList.class;
		} else if (SortedSet.class.equals(type)) {
			return TreeSet.class;
		} else if (Set.class.equals(type)) {
			return HashSet.class;
		}
		return null;
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
     	if (ArrayList.class.equals(collectionType)) {
			return Collections.unmodifiableList((List<?>) collection);
		} else if (TreeSet.class.equals(collectionType)) {
			return Collections.unmodifiableSortedSet((SortedSet<?>) collection);
		} else if (HashSet.class.equals(collectionType)) {
			return Collections.unmodifiableSet((Set<?>) collection);
		}
         throw new RuntimeException("Unable to handle "+collectionType);
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
      try
      {
         if (baseType.isPrimitive()) return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
      }
      catch (Exception e)
      {
         throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
      }
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
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
         }
         catch (IllegalAccessException e)
         {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);  
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), targetException);
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
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), e);
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), strVal, target), targetException);
         }
      }
      return null;
   }

   protected void throwProcessingException(String message, Throwable cause)
   {
      throw new BadRequestException(message, cause);
   }
}
