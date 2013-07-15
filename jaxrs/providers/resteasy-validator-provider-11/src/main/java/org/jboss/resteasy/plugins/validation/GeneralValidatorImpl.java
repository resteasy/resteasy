package org.jboss.resteasy.plugins.validation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.validation.GeneralValidator;

import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;

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
   
   /**
    * Used for resolving type parameters. Thread-safe.
    */
   private static final TypeResolver typeResolver = new TypeResolver();
   
   private Validator validator;
   private ConstraintTypeUtil util = new ConstraintTypeUtil11();
   private boolean isExecutableValidationEnabled;
   private ExecutableType[] defaultValidatedExecutableTypes;

   public GeneralValidatorImpl(Validator validator, boolean isExecutableValidationEnabled, Set<ExecutableType> defaultValidatedExecutableTypes)
   {
      this.validator = validator;
      this.isExecutableValidationEnabled = isExecutableValidationEnabled;
      this.defaultValidatedExecutableTypes = defaultValidatedExecutableTypes.toArray(new ExecutableType[]{});
   }

   @Override
   public void validate(HttpRequest request, Object object, Class<?>... groups)
   {
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      try
      {
         Set<ConstraintViolation<Object>> cvs = validator.validate(object, groups);
         for (Iterator<ConstraintViolation<Object>> it = cvs.iterator(); it.hasNext(); )
         {
            ConstraintViolation<Object> cv = it.next();
            Type ct = util.getConstraintType(cv);
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
         }
      }
      catch (Exception e)
      {
         ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
         violationsContainer.setException(e);
         throw new ResteasyViolationException(violationsContainer);
      }
      ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
      violationsContainer.addViolations(rcvs);
   }

   protected ViolationsContainer<Object> getViolationsContainer(HttpRequest request)
   {
      ViolationsContainer<Object> violationsContainer = ViolationsContainer.class.cast(request.getAttribute(ViolationsContainer.class.getName()));
      if (violationsContainer == null)
      {
         violationsContainer = new ViolationsContainer<Object>();
         request.setAttribute(ViolationsContainer.class.getName(), violationsContainer);
      }
      return violationsContainer;
   }

   @Override
   public void checkViolations(HttpRequest request)
   {
      ViolationsContainer<Object> violationsContainer = ViolationsContainer.class.cast(request.getAttribute(ViolationsContainer.class.getName()));
      if (violationsContainer != null && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer);
      }

   }

   @Override
   public void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups)
   {
      if (method.getParameterTypes().length == 0)
      {
         checkViolations(request);
         return;
      }

      ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      try
      {
         Set<ConstraintViolation<Object>> cvs = validator.forExecutables().validateParameters(object, method, parameterValues, groups);
         for (Iterator<ConstraintViolation<Object>> it = cvs.iterator(); it.hasNext(); )
         {
            ConstraintViolation<Object> cv = it.next();
            Type ct = util.getConstraintType(cv);
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), convertArrayToString(cv.getInvalidValue())));
         }
      }
      catch (Exception e)
      {
         violationsContainer.setException(e);
         throw new ResteasyViolationException(violationsContainer);
      }
      violationsContainer.addViolations(rcvs);
      if (violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer);
      }
   }

   @Override
   public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups)
   {
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
      try
      {
         Set<ConstraintViolation<Object>> cvs = validator.forExecutables().validateReturnValue(object, method, returnValue, groups);
         for (Iterator<ConstraintViolation<Object>> it = cvs.iterator(); it.hasNext(); )
         {
            ConstraintViolation<Object> cv = it.next();
            Type ct = util.getConstraintType(cv);
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
         }
      }
      catch (Exception e)
      {
         violationsContainer.setException(e);
         throw new ResteasyViolationException(violationsContainer);
      }
      violationsContainer.addViolations(rcvs);
      if (violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer);
      }
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
         // We start by examining the method itself.
         Method superMethod = getSuperMethod(method, clazz);
         if (superMethod != null)
         {
            ExecutableType[] types = getExecutableTypesOnMethod(superMethod);
            if (types != null)
            {
               typesList.add(types);
            }
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
   	   Method interfaceMethod = getSuperMethod(method, interfaces[i]);
   	   if (interfaceMethod != null)
   	   {
   	      ExecutableType[] types = getExecutableTypesOnMethod(interfaceMethod);
   	      if (types != null)
   	      {
   	         typesList.add(types);
   	      }
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
   
   static protected String convertArrayToString(Object o)
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
         result = (o == null ? "" : o.toString());
      }
      return result;
   }
   
   /**
    * Returns a super method, if any, of a method in a class.
    * Here, the "super" relationship is reflexive.  That is, a method
    * is a super method of itself.
    */
   static protected Method getSuperMethod(Method method, Class<?> clazz)
   {
      Method[] methods = clazz.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (overrides(method, methods[i]))
         {
            return methods[i];
         }
      }
      return null;
   }
   
	/**
	 * Checks, whether {@code subTypeMethod} overrides {@code superTypeMethod}.
	 * 
	 * N.B. "Override" here is reflexive. I.e., a method overrides itself.
	 * 
	 * @param subTypeMethod   The sub type method (cannot be {@code null}).
	 * @param superTypeMethod The super type method (cannot be {@code null}).
	 * 
	 * @return Returns {@code true} if {@code subTypeMethod} overrides {@code superTypeMethod}, {@code false} otherwise.
	 *         
	 * Taken from Hibernate Validator
	 */
   static protected boolean overrides(Method subTypeMethod, Method superTypeMethod)
   {
      if (subTypeMethod == null || superTypeMethod == null)
      {
         throw new RuntimeException("Expect two non-null methods");
      }

      if (!subTypeMethod.getName().equals(superTypeMethod.getName()))
      {
         return false;
      }

      if (subTypeMethod.getParameterTypes().length != superTypeMethod.getParameterTypes().length)
      {
         return false;
      }

      if (!superTypeMethod.getDeclaringClass().isAssignableFrom(subTypeMethod.getDeclaringClass()))
      {
         return false;
      }

      return parametersResolveToSameTypes(subTypeMethod, superTypeMethod);
   }

   /**
    * Taken from Hibernate Validator
    */
   static protected boolean parametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod)
   {
      if (subTypeMethod.getParameterTypes().length == 0)
      {
         return true;
      }

      ResolvedType resolvedSubType = typeResolver.resolve(subTypeMethod.getDeclaringClass());
      MemberResolver memberResolver = new MemberResolver(typeResolver);
      memberResolver.setMethodFilter(new SimpleMethodFilter(subTypeMethod, superTypeMethod));
      ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedSubType, null, null);
      ResolvedMethod[] resolvedMethods = typeWithMembers.getMemberMethods();

      // The ClassMate doc says that overridden methods are flattened to one
      // resolved method. But that is the case only for methods without any
      // generic parameters.
      if (resolvedMethods.length == 1)
      {
         return true;
      }

      // For methods with generic parameters I have to compare the argument
      // types (which are resolved) of the two filtered member methods.
      for (int i = 0; i < resolvedMethods[0].getArgumentCount(); i++)
      {

         if (!resolvedMethods[0].getArgumentType(i).equals(resolvedMethods[1].getArgumentType(i)))
         {
            return false;
         }
      }

      return true;
   }

   /**
    * A filter implementation filtering methods matching given methods.
    * 
    * @author Gunnar Morling
    * 
    * Taken from Hibernate Validator
    */
   static protected class SimpleMethodFilter implements Filter<RawMethod>
   {
      private final Method method1;
      private final Method method2;

      private SimpleMethodFilter(Method method1, Method method2)
      {
         this.method1 = method1;
         this.method2 = method2;
      }

      @Override
      public boolean include(RawMethod element)
      {
         return element.getRawMember().equals(method1) || element.getRawMember().equals(method2);
      }
   }
}
