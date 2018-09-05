package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;

import org.jboss.resteasy.spi.HttpRequest;

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
    * @param request http request
    * @param object object to validate
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link javax.validation.groups.Default})
    * @throws IllegalArgumentException if object is {@code null}
    *         or if {@code null} is passed to the varargs groups
    * @throws javax.validation.ValidationException if a non recoverable error happens
    *         during the validation process
    */
   void validate(HttpRequest request, Object object, Class<?>... groups);
   /**
    * Validates all constraints placed on the parameters of the given method.
    *
    * @param request http request
    * @param object the object on which the method to validate is invoked
    * @param method the method for which the parameter constraints is validated
    * @param parameterValues the values provided by the caller for the given method's
    *        parameters
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link javax.validation.groups.Default})
    * @throws IllegalArgumentException if {@code null} is passed for any of the parameters
    *         or if parameters don't match with each other
    * @throws javax.validation.ValidationException if a non recoverable error happens during the
    *         validation process
    */
   void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups);

   /**
    * Validates all return value constraints of the given method.
    *
    * @param request http request
    * @param object the object on which the method to validate is invoked
    * @param method the method for which the return value constraints is validated
    * @param returnValue the value returned by the given method
    * @param groups the group or list of groups targeted for validation (defaults to
    *        {@link javax.validation.groups.Default})
    * @throws IllegalArgumentException if {@code null} is passed for any of the object,
    *         method or groups parameters or if parameters don't match with each other
    * @throws javax.validation.ValidationException if a non recoverable error happens during the
    *         validation process
    */
   void validateReturnValue(
         HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups);

   /**
    * Indicates if validation is turned on for a class.
    * 
    * @param clazz Class to be examined
    * @return true if and only if validation is turned on for clazz
    */
   boolean isValidatable(Class<?> clazz);
     
   /**
    * Indicates if validation is turned on for a method.
    * 
    * @param method method to be examined
    * @return true if and only if validation is turned on for method
    */   
   boolean isMethodValidatable(Method method);

   /**
    * Throws a ResteasyViolationException if any validation violations have been detected.
    * 
    * @param request http request
    */
   void checkViolations(HttpRequest request);
}