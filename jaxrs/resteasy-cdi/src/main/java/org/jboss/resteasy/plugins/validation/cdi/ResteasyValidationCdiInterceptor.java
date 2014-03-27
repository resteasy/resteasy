package org.jboss.resteasy.plugins.validation.cdi;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.jboss.resteasy.util.IsHttpMethod;
import org.jboss.resteasy.util.Types;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 7, 2014
 */
@Interceptor
public class ResteasyValidationCdiInterceptor implements Serializable
{
   public static final String VALIDATE_SETTERS = "org.jboss.resteasy.validation.cdi.validateSetters";
   
   private final Logger log = Logger.getLogger(ResteasyValidationCdiInterceptor.class);
   
   private GeneralValidatorCDI validator;
   private boolean isValidatable;
   private boolean methodIsValidatable;
   
   
   public ResteasyValidationCdiInterceptor()
   {
      log.debug("creating ResteasyValidationCdiInterceptor");
      ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
      ContextResolver<GeneralValidatorCDI> resolver = providerFactory.getContextResolver(GeneralValidatorCDI.class, MediaType.WILDCARD_TYPE);
      if (resolver != null)
      {
         validator = providerFactory.getContextResolver(GeneralValidatorCDI.class, MediaType.WILDCARD_TYPE).getContext(null);
      }
   }
   
   @AroundInvoke
   public Object intercept(InvocationContext ctx) throws Exception
   {
      log.debug("*** Intercepting call in ResteasyValidationCdiInterceptor.intercept()");
      
      // It's not a resource method.
      if (!isResourceMethodOrLocator(ctx.getTarget().getClass(), ctx.getMethod()))
      {
         return invoke(ctx);
      }
      
      HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
      if (isSetter(ctx) && !Boolean.parseBoolean(request.getServletContext().getInitParameter(VALIDATE_SETTERS)))
      {
         return invoke(ctx);
      }
      
      MockHttpRequestCDI mockRequest = new MockHttpRequestCDI(request);
      if (validator != null)
      {
         isValidatable = validator.isValidatableFromCDI(ctx.getMethod().getDeclaringClass());
         methodIsValidatable = validator.isMethodValidatableFromCDI(ctx.getMethod());
         if (isValidatable)
         {
            validator.validate(mockRequest, ctx.getTarget());
         }
         if (methodIsValidatable)
         {
            validator.validateAllParameters(mockRequest, ctx.getTarget(), ctx.getMethod(), ctx.getParameters());
         }
         else if (isValidatable)
         {
            validator.checkViolations(mockRequest);
         }
      }
      
      Object result = invoke(ctx);
      
      log.debug("*** Back from intercepting call in ResteasyValidationCdiInterceptor.intercept()");
      if (validator != null)
      {
         validator.validateReturnValue(mockRequest, ctx.getTarget(), ctx.getMethod(), result);
      }
      return result;
   }
   
   protected boolean isSetter(InvocationContext ctx)
   {
      Method m = ctx.getMethod();
      return m.getName().startsWith("set") && m.getParameterTypes().length == 1;
   }
   
   /**
    * Borrowed from org.jboss.resteasy.spi.metadata.ResourceBuilder
    */
   protected static boolean isResourceMethodOrLocator(Class<?> root, Method implementation)
   {
      // check the method itself
      if (implementation.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(implementation) != null)
         return true;

      if (implementation.isAnnotationPresent(Produces.class) || implementation.isAnnotationPresent(Consumes.class))
      {
         // completely abort this method
         return false;
      }

      // Per http://download.oracle.com/auth/otn-pub/jcp/jaxrs-1.0-fr-oth-JSpec/jaxrs-1.0-final-spec.pdf
      // Section 3.2 Annotation Inheritance

      // Check possible superclass declarations
      for (Class<?> clazz = implementation.getDeclaringClass().getSuperclass(); clazz != null; clazz = clazz.getSuperclass())
      {
         try
         {
            Method method = clazz.getDeclaredMethod(implementation.getName(), implementation.getParameterTypes());
            if (method.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(method) != null)
               return true;
            if (method.isAnnotationPresent(Produces.class) || method.isAnnotationPresent(Consumes.class))
            {
               // completely abort this method
               return false;
            }
         }
         catch (NoSuchMethodException e)
         {
            // ignore
         }
      }

      // Not found yet, so next check ALL interfaces from the root,
      // but ensure no redefinition by peer interfaces (ambiguous) to preserve logic found in
      // original implementation
      for (Class<?> clazz = root; clazz != null; clazz = clazz.getSuperclass())
      {
         Method method = null;
         for (Class<?> iface : clazz.getInterfaces())
         {
            Method m = findAnnotatedInterfaceMethod(root, iface, implementation);
            if (m != null)
            {
               if(method != null && !m.equals(method))
                  throw new RuntimeException("Ambiguous inherited JAX-RS annotations applied to method: " + implementation);
               method = m;
            }
         }
         if (method != null)
            return true;
      }
      return false;
   }
   
   /**
    * Borrowed from org.jboss.resteasy.spi.metadata.ResourceBuilder
    */
   protected static Method findAnnotatedInterfaceMethod(Class<?> root, Class<?> iface, Method implementation)
   {
      for (Method method : iface.getMethods())
      {
         if (method.isSynthetic()) continue;

         if (!method.getName().equals(implementation.getName())) continue;
         if (method.getParameterTypes().length != implementation.getParameterTypes().length) continue;

         Method actual = Types.getImplementingMethod(root, method);
         if (!actual.equals(implementation)) continue;

         if (method.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(method) != null)
            return method;

      }
      for (Class<?> extended : iface.getInterfaces())
      {
         Method m = findAnnotatedInterfaceMethod(root, extended, implementation);
         if(m != null)
            return m;
      }
      return null;
   }
   
   protected Object invoke(InvocationContext ctx) throws Exception
   {
      Object result = null;
      try
      {
         result = ctx.proceed();
      }
      catch (ConstraintViolationException e)
      {
         // Thrown by Hibernate Validation CDI.
         // Ignore. We'll do it ourselves.
         result = e.getConstraintViolations().iterator().next().getInvalidValue();
      }
      return result;
   }
}

