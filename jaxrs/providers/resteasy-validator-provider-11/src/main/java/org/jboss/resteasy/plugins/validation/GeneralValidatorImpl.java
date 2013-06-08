package org.jboss.resteasy.plugins.validation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.BootstrapConfiguration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.validation.metadata.BeanDescriptor;

import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.plugins.providers.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.ConstraintType.Type;
import org.jboss.resteasy.spi.validation.ResteasyConstraintViolation;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 */
public class GeneralValidatorImpl implements GeneralValidator
{
   private static final Set<ResteasyConstraintViolation> empty = new HashSet<ResteasyConstraintViolation>();
   
   private Validator validator;
   private ConstraintTypeUtil util = new ConstraintTypeUtil11();
   private boolean isExecutableValidationEnabled;
   private ExecutableType[] defaultValidatedExecutableTypes;

   public GeneralValidatorImpl(Validator validator)
   {
      this.validator = validator;
      BootstrapConfiguration bc = Validation.byDefaultProvider().configure().getBootstrapConfiguration();
      isExecutableValidationEnabled = bc.isExecutableValidationEnabled();
      defaultValidatedExecutableTypes = bc.getDefaultValidatedExecutableTypes().toArray(new ExecutableType[]{});
   }

   @Override
   public <T> Set<ResteasyConstraintViolation> validate(T object, Class<?>... groups)
   {
      Set<ConstraintViolation<T>> cvs = validator.validate(object, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      for (Iterator<ConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
      {
         ConstraintViolation<T> cv = it.next();
         Type ct = util.getConstraintType(cv);
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      return rcvs;
   }

   @Override
   public <T> Set<ResteasyConstraintViolation> validateProperty(T object, String propertyName, Class<?>... groups)
   {
      Set<ConstraintViolation<T>> cvs = validator.validateProperty(object, propertyName, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      for (Iterator<ConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
      {
         ConstraintViolation<T> cv = it.next();
         Type ct = util.getConstraintType(cv);
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      return rcvs;
   }

   @Override
   public <T> Set<ResteasyConstraintViolation> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups)
   {
      Set<ConstraintViolation<T>> cvs = validator.validateValue(beanType, propertyName, value, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      for (Iterator<ConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
      {
         ConstraintViolation<T> cv = it.next();
         Type ct = util.getConstraintType(cv);
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      return rcvs;
   }

   @Override
   public BeanDescriptor getConstraintsForClass(Class<?> clazz)
   {
      return validator.getConstraintsForClass(clazz);
   }
   
   @Override
   public <T> T unwrap(Class<T> type)
   {
      return validator.unwrap(type);
   }

   @Override
   public <T> Set<ResteasyConstraintViolation> validateAllParameters(T object, Method method, Object[] parameterValues, Class<?>... groups)
   {
      if (method.getParameterTypes().length == 0)
      {
         return empty;
      }
      Set<ConstraintViolation<T>> cvs = validator.forExecutables().validateParameters(object, method, parameterValues, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      for (Iterator<ConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
      {
         ConstraintViolation<T> cv = it.next();
         Type ct = util.getConstraintType(cv);
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), convertArrayToString(cv.getInvalidValue())));
      }
      return rcvs;
   }

   @Override
   public <T> Set<ResteasyConstraintViolation> validateReturnValue(T object, Method method, Object returnValue, Class<?>... groups)
   {
      Set<ConstraintViolation<T>> cvs = validator.forExecutables().validateReturnValue(object, method, returnValue, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      for (Iterator<ConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
      {
         ConstraintViolation<T> cv = it.next();
         Type ct = util.getConstraintType(cv);
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      return rcvs;
   }


   @Override
   public boolean isValidatable(Class<?> clazz)
   {
      return true;
   }
   
   @Override
   public boolean isMethodValidatable(Method m)
   {
   	if (!isExecutableValidationEnabled)
   	{
   		return false;
   	}
   	
   	ExecutableType[] types = null;
      List<ExecutableType[]> typesList = getExecutableTypesOnMethodInHierarchy(m);
      if (typesList.size() > 1)
      {
      	throw new ValidationException("@ValidateOnExecution found on multiple overridden methods");
      }
      if (typesList.size() == 1)
      {
      	types = typesList.get(0);
      }
      else
      {
      	ValidateOnExecution voe = m.getDeclaringClass().getAnnotation(ValidateOnExecution.class);
      	if (voe == null)
      	{
      		types = defaultValidatedExecutableTypes;
      	}
      	else
      	{
      		if (voe.type().length > 0)
      		{
      			types = voe.type();
      		}
      		else
      		{
      			types = defaultValidatedExecutableTypes;
      		}
      	}
      }
      
      boolean isGetterMethod = isGetter(m);
      for (int i = 0; i < types.length; i++)
      {
         switch (types[i])
         {
            case IMPLICIT:
            case ALL:
               return true;
               
            case NONE:
               continue;
               
            case NON_GETTER_METHODS:
               if (!isGetterMethod)
               {
                  return true;
               }
               continue;
               
            case GETTER_METHODS:
               if (isGetterMethod)
               {
                  return true;
               }
               continue;
               
            default: 
               continue;
         }
      }
      return false;
   }
   
   static protected List<ExecutableType[]> getExecutableTypesOnMethodInHierarchy(Method method)
   {
      Class<?> clazz = method.getDeclaringClass();
      List<ExecutableType[]> typesList = new ArrayList<ExecutableType[]>();
      while (clazz != null)
      {
         try
         {
            Method superMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (superMethod != null)
            {
               ExecutableType[] types = getExecutableTypesOnMethod(superMethod);
               if (types != null)
               {
               	typesList.add(types);
               }
            }
         }
         catch (NoSuchMethodException e)
         {
            // Ignore.
         }
         
         typesList.addAll(getExecutableTypesOnMethodInInterfaces(clazz, method));
         clazz = clazz.getSuperclass();
      }
      return typesList;
   }
   
   static protected List<ExecutableType[]> getExecutableTypesOnMethodInInterfaces(Class<?> clazz, Method method)
   {
   	List<ExecutableType[]> typesList = new ArrayList<ExecutableType[]>();
   	Class<?>[] interfaces = clazz.getInterfaces();
   	for (int i = 0; i < interfaces.length; i++)
   	{
         Method declaredMethod;
         try
         {
	         declaredMethod = interfaces[i].getDeclaredMethod(method.getName(), method.getParameterTypes());
	         ExecutableType[] types = getExecutableTypesOnMethod(declaredMethod);
	         if (types != null)
	         {
	         	typesList.add(types);
	         }
         }
         catch (NoSuchMethodException e)
         {
         	// Ignore.
         }
         List<ExecutableType[]> superList = getExecutableTypesOnMethodInInterfaces(interfaces[i], method);
         if (superList.size() > 0)
         {
         	typesList.addAll(superList);
         }
   	}
   	return typesList;
   }
   
   static protected ExecutableType[] getExecutableTypesOnMethod(Method method)
   {
   	ValidateOnExecution voe = method.getAnnotation(ValidateOnExecution.class);
   	if (voe == null || voe.type().length == 0)
   	{
   		return null;
   	}
   	ExecutableType[] types = voe.type();
   	if (types == null || types.length == 0)
   	{
   		return null;
   	}
   	return types;
   }
   
   static protected boolean isGetter(Method m)
   {
      String name = m.getName();
      Class<?> returnType = m.getReturnType();
      if (returnType.equals(Void.class))
      {
         return false;
      }
      if (m.getParameterTypes().length > 0)
      {
         return false;
      }
      if (name.startsWith("get"))
      {
         return true;
      }
      if (name.startsWith("is") && returnType.equals(boolean.class))
      {
         return true;
      }
      return false;
   }
   
   protected String convertArrayToString(Object o)
   {
      String result = null;
      if (o instanceof Object[])
      {
         Object[] array = Object[].class.cast(o);
         StringBuffer sb = new StringBuffer("[").append(convertArrayToString(array[0]));
         for (int i = 1; i < array.length; i++)
         {
            sb.append(", ").append(convertArrayToString(array[i]));
         }
         sb.append("]");
         result = sb.toString();
      }
      else
      {
         result = o.toString();
      }
      return result;
   }
}
