package org.jboss.resteasy.plugins.providers.validation;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;
import org.hibernate.validator.method.metadata.TypeDescriptor;
import org.jboss.resteasy.spi.validation.GeneralValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.lang.reflect.Method;
import java.util.Set;

/** 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 7, 2012
 */
public class GeneralValidatorImpl implements GeneralValidator
{
   private Validator validator;
   private MethodValidator methodValidator;
   
   public GeneralValidatorImpl(Validator validator, MethodValidator methodValidator)
   {
      this.validator = validator;
      this.methodValidator = methodValidator;
   }
   
   @Override
   public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups)
   {
      return validator.validate(object, groups);
   }

   @Override
   public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups)
   {
      return validator.validateProperty(object, propertyName, groups);
   }

   @Override
   public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups)
   {
      return validator.validateValue(beanType, propertyName, value, groups);
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
   public <T> Set<MethodConstraintViolation<T>> validateParameter(T object, Method method, Object parameterValue, int parameterIndex, Class<?>... groups)
   {
      return methodValidator.validateParameter(object, method, parameterValue, parameterIndex, groups);
   }

   @Override
   public <T> Set<MethodConstraintViolation<T>> validateAllParameters(T object, Method method, Object[] parameterValues, Class<?>... groups)
   {
      return methodValidator.validateAllParameters(object, method, parameterValues, groups);
   }

   @Override
   public <T> Set<MethodConstraintViolation<T>> validateReturnValue(T object, Method method, Object returnValue, Class<?>... groups)
   {
      return methodValidator.validateReturnValue(object, method, returnValue, groups);
   }

   @Override
   public TypeDescriptor getConstraintsForType(Class<?> clazz)
   {
      return methodValidator.getConstraintsForType(clazz);
   }
}
