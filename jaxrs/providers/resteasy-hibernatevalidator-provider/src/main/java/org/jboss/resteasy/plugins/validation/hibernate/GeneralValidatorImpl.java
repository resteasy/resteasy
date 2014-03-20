package org.jboss.resteasy.plugins.validation.hibernate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.validation.GeneralValidator;
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
public class GeneralValidatorImpl implements GeneralValidatorCDI
{
   private Validator validator;
   private MethodValidator methodValidator;
   private ConstraintTypeUtil util = new ConstraintTypeUtil10();
   private boolean cdiActive;

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
      if (violationsContainer.size() > 0)
      {
         throw new ResteasyViolationException(violationsContainer, request.getHttpHeaders().getAcceptableMediaTypes());
      }
   }

   @Override
   public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups)
   {
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
      // Called from resteasy-jaxrs. Only validate subresources.
      if (cdiActive)
      {
         if (GetRestful.isRootResource(m.getDeclaringClass()) || !GetRestful.isSubResourceClass(m.getDeclaringClass()))
         {
            return false;
         }
      }
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
}
