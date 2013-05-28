package org.jboss.resteasy.plugins.validation;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.spi.validation.ConstraintType.Type;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.validation.ResteasyConstraintViolation;

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

   public GeneralValidatorImpl(Validator validator)
   {
      this.validator = validator;
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
   public <T> Set<ResteasyConstraintViolation> validateParameter(T object, Method method, Object parameterValue, int parameterIndex, Class<?>... groups)
   {
//      Set<ConstraintViolation<T>> cvs = validator.forExecutables().v(object, method, parameterValues, groups)(object, method, parameterValue, parameterIndex, groups);
      Set<ResteasyConstraintViolation> rcvs = new HashSet<ResteasyConstraintViolation>();
//      for (Iterator<MethodConstraintViolation<T>> it = cvs.iterator(); it.hasNext(); )
//      {
//         ConstraintViolation<T> cv = it.next();
//         Type ct = util.getConstraintType(cv);
//         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
//      }
      return rcvs;
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
         rcvs.add(new ResteasyConstraintViolation(ct, cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
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

//   @Override
//   public TypeDescriptor getConstraintsForType(Class<?> clazz)
//   {
//      return methodValidator.getConstraintsForType(clazz);
//   }
}
