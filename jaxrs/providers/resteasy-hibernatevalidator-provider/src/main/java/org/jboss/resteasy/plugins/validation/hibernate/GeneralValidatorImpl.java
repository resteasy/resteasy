package org.jboss.resteasy.plugins.validation.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.GetRestful;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 */
@SuppressWarnings("serial")
public class GeneralValidatorImpl implements GeneralValidatorCDI
{
   private Validator validator;
   private MethodValidator methodValidator;
   private ConstraintTypeUtil util = new ConstraintTypeUtil10();
   private boolean cdiActive;
   
   public abstract static class S1 extends AnnotationLiteral<Stateless> implements Stateless { }
   public static final Annotation STATELESS = new S1() 
   {
      @Override public String name() {return null;}
      @Override public String mappedName() {return null;}
      @Override public String description() {return null;}
   };

   public abstract static class S2 extends AnnotationLiteral<Stateful> implements Stateful { }
   public static final Annotation STATEFUL = new S2() 
   {
      @Override public String name() {return null;}
      @Override public String mappedName() {return null;}
      @Override public String description() {return null;}
   };

   public abstract static class S3 extends AnnotationLiteral<Stateful> implements Stateful { }
   public static final Annotation SINGLETON = new S3() 
   {
      @Override public String name() {return null;}
      @Override public String mappedName() {return null;}
      @Override public String description() {return null;}
   };
   
   public GeneralValidatorImpl(Validator validator, MethodValidator methodValidator)
   {
      this.validator = validator;
      this.methodValidator = methodValidator;
      try
      {
         cdiActive = ResteasyCdiExtension.isCDIActive();
      }
      catch (Exception e)
      {
         // Intentionally empty. In case ResteasyCdiExtension is not on the classpath.
      }
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


   public void checkViolations(HttpRequest request)
   {
      if (cdiActive)
      {
         return;
      }
      doCheckViolations(request);
   }

   @Override
   public void checkViolationsfromCDI(HttpRequest request)
   {
      assert(cdiActive);
      doCheckViolations(request);
   }
   
   protected void doCheckViolations(HttpRequest request)
   {
      @SuppressWarnings("unchecked")
      ViolationsContainer<Object> violationsContainer = ViolationsContainer.class.cast(request.getAttribute(ViolationsContainer.class.getName()));
      if (violationsContainer != null && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
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
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), (cv.getInvalidValue() == null ? "null" :cv.getInvalidValue().toString())));
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
      /*
      if (rcvs.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer);
      }
      */
   }

   @Override
   public void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups)
   {
      if (isSessionBean(method.getDeclaringClass()))
      {
         try
         {
            // This hack is for Hibernate Validator 4.x, which looks for the method in the bean interface.
            method = object.getClass().getMethod(method.getName(), method.getParameterTypes());
         } 
         catch (NoSuchMethodException e1)
         {
            // 
         }
      }
      
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
      try
      {
         Set<MethodConstraintViolation<Object>> cvs = methodValidator.validateAllParameters(object, method, parameterValues, groups);
         for (Iterator<MethodConstraintViolation<Object>> it = cvs.iterator(); it.hasNext(); )
         {
            ConstraintViolation<Object> cv = it.next();
            Type ct = util.getConstraintType(cv);
            Object o = cv.getInvalidValue();
            String value = (o == null ? "" : o.toString());
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), value));
         }
      }
      catch (Exception e)
      {
         violationsContainer.setException(e);
         throw new ResteasyViolationException(violationsContainer);
      }
      violationsContainer.addViolations(rcvs);
      if (!cdiActive && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }

   @Override
   public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups)
   {
      System.out.println("declaring class: " + method.getDeclaringClass());
      if (isSessionBean(method.getDeclaringClass()))
      {
         try
         {
            // This hack is for Hibernate Validator 4.x, which looks for the method in the bean interface.
            method = object.getClass().getMethod(method.getName(), method.getParameterTypes());
         } 
         catch (NoSuchMethodException e1)
         {
            // 
         }
      }
      
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
      ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
      try
      {
         Set<MethodConstraintViolation<Object>> cvs = methodValidator.validateReturnValue(object, method, returnValue, groups);
         for (Iterator<MethodConstraintViolation<Object>> it = cvs.iterator(); it.hasNext(); )
         {
            ConstraintViolation<Object> cv = it.next();
            Type ct = util.getConstraintType(cv);
            Object o = cv.getInvalidValue();
            String value = (o == null ? "" : o.toString());
            rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), value));
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
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }
   
   @Override
   public boolean isValidatable(Class<?> clazz)
   {
      if (cdiActive)
      {
         return !GetRestful.isRootResource(clazz) && GetRestful.isSubResourceClass(clazz);
      }
      return checkIsValidatable(clazz);
   }
   
   @Override
   public boolean isValidatableFromCDI(Class<?> clazz)
   {
      assert(cdiActive);
      return checkIsValidatable(clazz);
   }
   
   protected boolean checkIsValidatable(Class<?> clazz)
   {
      ValidateRequest resourceValidateRequest = FindAnnotation.findAnnotation(clazz.getAnnotations(), ValidateRequest.class);
      DoNotValidateRequest doNotValidateRequest = FindAnnotation.findAnnotation(clazz.getAnnotations(), DoNotValidateRequest.class);
      return resourceValidateRequest != null && doNotValidateRequest == null; 
   }
   
   @Override
   public boolean isMethodValidatable(Method m)
   {
      return checkIsMethodValidatable(m);
   }
   
   @Override
   public boolean isMethodValidatableFromCDI(Method m)
   {
      assert(cdiActive);
      return checkIsMethodValidatable(m);
   }
   
   protected boolean checkIsMethodValidatable(Method m)
   {
      ValidateRequest resourceValidateRequest = FindAnnotation.findAnnotation(m.getDeclaringClass().getAnnotations(), ValidateRequest.class);
      ValidateRequest methodValidateRequest = FindAnnotation.findAnnotation(m.getAnnotations(), ValidateRequest.class);
      DoNotValidateRequest doNotValidateRequest = FindAnnotation.findAnnotation(m.getAnnotations(), DoNotValidateRequest.class);
      return (resourceValidateRequest != null || methodValidateRequest != null) && doNotValidateRequest == null;
   }
   
   @Override
   public void checkForConstraintViolations(HttpRequest request, Exception e)
   {
      if (e instanceof ConstraintViolationException)
      {
         ConstraintViolationException cve = ConstraintViolationException.class.cast(e);
         Set<ConstraintViolation<?>> cvs = cve.getConstraintViolations();
         Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
         try
         {
            for (Iterator<ConstraintViolation<?>> it = cvs.iterator(); it.hasNext(); )
            {
               ConstraintViolation<?> cv = it.next();
               Type ct = util.getConstraintType(cv);
               rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), (cv.getInvalidValue() == null ? "null" :cv.getInvalidValue().toString())));
            }
         }
         catch (Exception e1)
         {
            ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
            violationsContainer.setException(e);
            throw new ResteasyViolationException(violationsContainer);
         }
         if (rcvs.size() > 0)
         {
            ViolationsContainer<Object> violationsContainer = getViolationsContainer(request);
            violationsContainer.addViolations(rcvs);
            throw new ResteasyViolationException(violationsContainer);
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
   
   private boolean isSessionBean(Class<?> clazz)
   {
      while (clazz != null)
      {
         Annotation[] as = clazz.getAnnotations();
         for (int i = 0; i < as.length; i++)
         {
            System.out.println("annotation[" + i + "]: " + as[i]);
         }
         if (clazz.getAnnotation(STATELESS.annotationType()) != null 
               || clazz.getAnnotation(STATEFUL.annotationType()) != null
               || clazz.getAnnotation(SINGLETON.annotationType()) != null)
         {
            return true;
         }
         clazz = clazz.getSuperclass();
      }
      return false;
   }
}
