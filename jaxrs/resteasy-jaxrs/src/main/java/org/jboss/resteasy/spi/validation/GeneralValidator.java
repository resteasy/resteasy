package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Javadoc adapted from javax.validation.Validator and javax.validation.executable.ExecutableValidator:
 * 
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 */
public interface GeneralValidator
{
   /**
    * Validates all constraints on {@code object}.
    *
    * @param object object to validate
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link Default})
    * @return constraint violations or an empty set if none
    * @throws IllegalArgumentException if object is {@code null}
    *         or if {@code null} is passed to the varargs groups
    * @throws ValidationException if a non recoverable error happens
    *         during the validation process
    */
   public abstract <T> Set<ResteasyConstraintViolation> validate(T object, Class<?>... groups);

    /**
    * Validates all constraints placed on the property of {@code object}
    * named {@code propertyName}.
    *
    * @param object object to validate
    * @param propertyName property to validate (i.e. field and getter constraints)
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link Default})
    * @return constraint violations or an empty set if none
    * @throws IllegalArgumentException if {@code object} is {@code null},
    *         if {@code propertyName} is {@code null}, empty or not a valid object property
    *         or if {@code null} is passed to the varargs groups
    * @throws ValidationException if a non recoverable error happens
    *         during the validation process
    */
   public abstract <T> Set<ResteasyConstraintViolation> validateProperty(T object,
         String propertyName, Class<?>... groups);

     /**
    * Validates all constraints placed on the property named {@code propertyName}
    * of the class {@code beanType} would the property value be {@code value}.
    * <p/>
    * {@link ConstraintViolation} objects return {@code null} for
    * {@link ConstraintViolation#getRootBean()} and {@link ConstraintViolation#getLeafBean()}.
    *
    * @param beanType the bean type
    * @param propertyName property to validate
    * @param value property value to validate
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link Default}).
    * @return constraint violations or an empty set if none
    * @throws IllegalArgumentException if {@code beanType} is {@code null},
    *         if {@code propertyName} is {@code null}, empty or not a valid object property
    *         or if {@code null} is passed to the varargs groups
    * @throws ValidationException if a non recoverable error happens
    *         during the validation process
    */
   public abstract <T> Set<ResteasyConstraintViolation> validateValue(
         Class<T> beanType, String propertyName, Object value,
         Class<?>... groups);

    /**
    * Returns the descriptor object describing bean constraints.
    * <p/>
    * The returned object (and associated objects including
    * {@link ConstraintDescriptor}s) are immutable.
    *
    * @param clazz class or interface type evaluated
    * @return the bean descriptor for the specified class
    * @throws IllegalArgumentException if clazz is {@code null}
    * @throws ValidationException if a non recoverable error happens
    *         during the metadata discovery or if some
    *         constraints are invalid.
    */
   public abstract BeanDescriptor getConstraintsForClass(Class<?> clazz);

    /**
    * Returns an instance of the specified type allowing access to
    * provider-specific APIs.
    * <p/>
    * If the Bean Validation provider implementation does not support
    * the specified class, {@link ValidationException} is thrown.
    *
    * @param type the class of the object to be returned
    * @return an instance of the specified class
    * @throws ValidationException if the provider does not support the call
    */
   public abstract <T> T unwrap(Class<T> type);

   /**
    * Validates all constraints placed on the parameters of the given method.
    *
    * @param <T> the type hosting the method to validate
    * @param object the object on which the method to validate is invoked
    * @param method the method for which the parameter constraints is validated
    * @param parameterValues the values provided by the caller for the given method's
    *        parameters
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link Default})
    * @return a set with the constraint violations caused by this validation;
    *         will be empty if no error occurs, but never {@code null}
    * @throws IllegalArgumentException if {@code null} is passed for any of the parameters
    *         or if parameters don't match with each other
    * @throws ValidationException if a non recoverable error happens during the
    *         validation process
    */
   public abstract <T> Set<ResteasyConstraintViolation> validateAllParameters(
         T object, Method method, Object[] parameterValues,
         Class<?>... groups);

   /**
    * Validates all return value constraints of the given method.
    *
    * @param <T> the type hosting the method to validate
    * @param object the object on which the method to validate is invoked
    * @param method the method for which the return value constraints is validated
    * @param returnValue the value returned by the given method
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link Default})
    * @return a set with the constraint violations caused by this validation;
    *         will be empty if no error occurs, but never {@code null}
    * @throws IllegalArgumentException if {@code null} is passed for any of the object,
    *         method or groups parameters or if parameters don't match with each other
    * @throws ValidationException if a non recoverable error happens during the
    *         validation process
    */
   public abstract <T> Set<ResteasyConstraintViolation> validateReturnValue(
         T object, Method method, Object returnValue, Class<?>... groups);

   /**
    * Indicates if validation is turned on for a class.
    * 
    * @param clazz Class to be examined
    * @return true if and only if validation is turned on for clazz
    */
   public abstract boolean isValidatable(Class<?> clazz);
     
   /**
    * Indicates if validation is turned on for a method.
    * 
    * @param method method to be examined
    * @return true if and only if validation is turned on for method
    */   
   public abstract boolean isMethodValidatable(Method method);
}