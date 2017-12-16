package org.jboss.resteasy.plugins.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.GroupDefinitionException;
import javax.validation.MessageInterpolator;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;

import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.plugins.validation.i18n.LogMessages;
import org.jboss.resteasy.plugins.validation.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.jboss.resteasy.util.GetRestful;

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
public class GeneralValidatorImpl implements GeneralValidatorCDI
{
   public static final String SUPPRESS_VIOLATION_PATH = "resteasy.validation.suppress.path";
   
   /**
    * Used for resolving type parameters. Thread-safe.
    */
   private TypeResolver typeResolver = new TypeResolver();
   private ValidatorFactory validatorFactory;
   private boolean isExecutableValidationEnabled;
   private ExecutableType[] defaultValidatedExecutableTypes;
   private boolean suppressPath;
   private boolean cdiActive;

   public GeneralValidatorImpl(ValidatorFactory validatorFactory, boolean isExecutableValidationEnabled, Set<ExecutableType> defaultValidatedExecutableTypes)
   {
      this.validatorFactory = validatorFactory;
      this.isExecutableValidationEnabled = isExecutableValidationEnabled;
      this.defaultValidatedExecutableTypes = defaultValidatedExecutableTypes.toArray(new ExecutableType[]{});
      
      try
      {
         cdiActive = ResteasyCdiExtension.isCDIActive();
         LogMessages.LOGGER.debug(Messages.MESSAGES.resteasyCdiExtensionOnClasspath());
      }
      catch (Throwable t)
      {
         // In case ResteasyCdiExtension is not on the classpath.
         LogMessages.LOGGER.debug(Messages.MESSAGES.resteasyCdiExtensionNotOnClasspath());
      }
      
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null)
      {
         String s = context.getParameter(SUPPRESS_VIOLATION_PATH);
         if (s != null)
         {
            suppressPath = Boolean.parseBoolean(s);
         }
      }
   }

   @Override
   public void validate(HttpRequest request, Object object, Class<?>... groups)
   {
      Validator validator = getValidator(request);
      Set<ConstraintViolation<Object>> cvs = null;
      
      try
      {
         cvs = validator.validate(object, groups);
      }
      catch (Exception e)
      {
         SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object);
         violationsContainer.setException(e);
         violationsContainer.setFieldsValidated(true);
         throw toValidationException(e, violationsContainer);
      }
      
      SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object);
      violationsContainer.addViolations(cvs);
      violationsContainer.setFieldsValidated(true);
   }

   private ValidationException toValidationException(Exception exception, SimpleViolationsContainer simpleViolationsContainer)
   {
      if (exception instanceof ConstraintDeclarationException ||
          exception instanceof ConstraintDefinitionException  ||
          exception instanceof GroupDefinitionException)
      {
         return (ValidationException) exception;
      }
      return new ResteasyViolationException(simpleViolationsContainer);
   }

   @Override
   public void checkViolations(HttpRequest request)
   {
      // Called from resteasy-jaxrs only if two argument version of isValidatable() returns true.
      SimpleViolationsContainer violationsContainer = getViolationsContainer(request, null);
      Object target = violationsContainer.getTarget();
      if (target != null && violationsContainer.isFieldsValidated())
      {
         if (violationsContainer != null && violationsContainer.size() > 0)
         {
            throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
         }
      }
   }
   
   @Override
   public void checkViolationsfromCDI(HttpRequest request)
   {
      if (request == null)
      {
         return;
      }
      
      SimpleViolationsContainer violationsContainer = SimpleViolationsContainer.class.cast(request.getAttribute(SimpleViolationsContainer.class.getName()));
      if (violationsContainer != null && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }

   @Override
   public void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups)
   {
      Validator validator = getValidator(request);
      SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object);

      if (method.getParameterTypes().length == 0)
      {
         checkViolations(request);
         return;
      }

      Set<ConstraintViolation<Object>> cvs = null;
      
      try
      {
         cvs = validator.forExecutables().validateParameters(object, method, parameterValues, groups);
      }
      catch (Exception e)
      {
         violationsContainer.setException(e);
         throw toValidationException(e, violationsContainer);
      }
      violationsContainer.addViolations(cvs);
      if ((violationsContainer.isFieldsValidated()
            || !GetRestful.isRootResource(object.getClass())
            || hasApplicationScope(object))
          && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }

   @Override
   public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups)
   {
      Validator validator = getValidator(request);
      SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object);
      Set<ConstraintViolation<Object>> cvs = null;
      
      try
      {
         cvs = validator.forExecutables().validateReturnValue(object, method, returnValue, groups);
      }
      catch (Exception e)
      {
         violationsContainer.setException(e);
         throw toValidationException(e, violationsContainer);
      }
      violationsContainer.addViolations(cvs);
      if (violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }
   
   @Override
   public boolean isValidatable(Class<?> clazz)
   {
      // Called from resteasy-jaxrs.
      if (cdiActive)
      {
         return false;
      }
      return true;
   }
   

   @Override
   public boolean isValidatable(Class<?> clazz, InjectorFactory injectorFactory)
   {
      try
      {
         // Called from resteasy-jaxrs.
         if (cdiActive && injectorFactory instanceof CdiInjectorFactory)
         {
            return false;
         }
      }
      catch (NoClassDefFoundError e)
      {
        // Shouldn't get here. Deliberately empty.
      }
      return true;
   }

   @Override
   public boolean isValidatableFromCDI(Class<?> clazz)
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
         throw new ValidationException(Messages.MESSAGES.validateOnExceptionOnMultipleMethod());
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
   
   protected List<ExecutableType[]> getExecutableTypesOnMethodInHierarchy(Method method)
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
   
   protected List<ExecutableType[]> getExecutableTypesOnMethodInInterfaces(Class<?> clazz, Method method)
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
   protected Method getSuperMethod(Method method, final Class<?> clazz)
   {
      Method[] methods = new Method[0];
      try {
         if (System.getSecurityManager() == null) {
            methods = clazz.getDeclaredMethods();
         } else {
            methods = AccessController.doPrivileged(new PrivilegedExceptionAction<Method[]>() {
               @Override
               public Method[] run() throws Exception {
                  return clazz.getDeclaredMethods();
               }
            });
         }
      } catch (PrivilegedActionException pae) {

      }

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
   protected boolean overrides(Method subTypeMethod, Method superTypeMethod)
   {
      if (subTypeMethod == null || superTypeMethod == null)
      {
         throw new RuntimeException(Messages.MESSAGES.expectTwoNonNullMethods());
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
   protected boolean parametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod)
   {
      if (subTypeMethod.getParameterTypes().length == 0)
      {
         return true;
      }

      ResolvedType resolvedSubType = typeResolver.resolve(subTypeMethod.getDeclaringClass());
      MemberResolver memberResolver = new MemberResolver(typeResolver);
      memberResolver.setMethodFilter(new SimpleMethodFilter(subTypeMethod, superTypeMethod));
      final ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedSubType, null, null);
      ResolvedMethod[] resolvedMethods = new ResolvedMethod[0];
      try {
         if (System.getSecurityManager() == null) {
            resolvedMethods = typeWithMembers.getMemberMethods();
         } else {
            resolvedMethods = AccessController.doPrivileged(new PrivilegedExceptionAction<ResolvedMethod[]>() {
               @Override
               public ResolvedMethod[] run() throws Exception {
                  return typeWithMembers.getMemberMethods();
               }
            });
         }
      } catch (PrivilegedActionException pae) {

      }

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
   
   @Override
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void checkForConstraintViolations(HttpRequest request, Exception e)
   {
      if (e instanceof InvocationTargetException)
      {
         Throwable t = InvocationTargetException.class.cast(e).getTargetException();
         if (t instanceof ConstraintViolationException)
         {
            e = ConstraintViolationException.class.cast(t);
         }
      }
      
      if (e instanceof ConstraintViolationException)
      {
         SimpleViolationsContainer violationsContainer = getViolationsContainer(request, null);
         ConstraintViolationException cve = ConstraintViolationException.class.cast(e);
         Set cvs = cve.getConstraintViolations();
         violationsContainer.addViolations(cvs);
         if (violationsContainer.size() > 0)
         {
            throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
         }
      }
      
      Throwable t = e.getCause();
      while (t != null && !(t instanceof ResteasyViolationException))
      {
         t = t.getCause();    
      }
      if (t instanceof ResteasyViolationException)
      {
         throw ResteasyViolationException.class.cast(t);
      }
   }
   
   protected Validator getValidator(HttpRequest request)
   {
      Validator v = Validator.class.cast(request.getAttribute(Validator.class.getName()));
      if (v == null) {
         Locale locale = getLocale(request);
         if (locale == null)
         {
            v = validatorFactory.getValidator();
         }
         else
         {
            MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(validatorFactory.getMessageInterpolator(), locale);
            v = validatorFactory.usingContext().messageInterpolator(interpolator).getValidator();
         }
         request.setAttribute(Validator.class.getName(), v);
      }
      return v;
   }

   protected SimpleViolationsContainer getViolationsContainer(HttpRequest request, Object target)
   {
      if (request == null)
      {
         return new SimpleViolationsContainer(target);
      }

      SimpleViolationsContainer violationsContainer = SimpleViolationsContainer.class.cast(request.getAttribute(SimpleViolationsContainer.class.getName()));
      if (violationsContainer == null)
      {
         violationsContainer = new SimpleViolationsContainer(target);
         request.setAttribute(SimpleViolationsContainer.class.getName(), violationsContainer);
      }
      return violationsContainer;
   }
   
   private Locale getLocale(HttpRequest request) {
      if (request == null)
      {
         return null;
      }
      List<Locale> locales = request.getHttpHeaders().getAcceptableLanguages();
      Locale locale = locales == null || locales.isEmpty() ? null : locales.get(0);
      return locale;
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

   static protected class LocaleSpecificMessageInterpolator implements MessageInterpolator {
      private final MessageInterpolator interpolator;
      private final Locale locale;

      public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale)
      {
         this.interpolator = interpolator;
         this.locale = locale;
      }

      @Override
      public String interpolate(String messageTemplate, Context context)
      {
         return interpolator.interpolate(messageTemplate, context, locale);
      }

      @Override
      public String interpolate(String messageTemplate, Context context, Locale locale)
      {
         return interpolator.interpolate(messageTemplate, context, locale);
      }
   }
   
   ResteasyConstraintViolation createResteasyConstraintViolation(ConstraintViolation<?> cv, Type ct)
   {
      String path = (suppressPath ? "*" : cv.getPropertyPath().toString());
      ResteasyConstraintViolation rcv = new ResteasyConstraintViolation(ct, path, cv.getMessage(), (cv.getInvalidValue() == null ? "null" :cv.getInvalidValue().toString()));
      return rcv;
   }

   private boolean hasApplicationScope(Object o)
   {
      Class<?> clazz = o.getClass();
      return clazz.getAnnotation(ApplicationScoped.class) != null;
   }
}
