package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;

import org.jboss.resteasy.spi.validation.GeneralValidator;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * If CDI is enabled, validation will be invoked from an interceptor,
 * rather than from ResourceMethodInvoker and MethodInjectorImpl.
 *
 * Copyright Feb 12, 2014
 */
public interface GeneralValidatorCDI extends GeneralValidator
{
   /**
    * Indicates if validation is turned on for a class.
    * This method should be called only from a CDI interceptor
    * 
    * @param clazz Class to be examined
    * @return true if and only if validation is turned on for clazz
    */
   public abstract boolean isValidatableFromCDI(Class<?> clazz);
     
   /**
    * Indicates if validation is turned on for a method.
    * This method should be called only from a CDI interceptor
    * 
    * @param method method to be examined
    * @return true if and only if validation is turned on for method
    */   
   public abstract boolean isMethodValidatableFromCDI(Method method);
}