package org.jboss.resteasy.spi.validation;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;

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
    * 
    * This method should be called from the resteasy-jaxrs module. It should
    * test if injectorFactor is an instance of CdiInjectorFactory, which indicates
    * that CDI is active.  If so, it should return false. Otherwise, it should
    * return the same value returned by GeneralValidator.isValidatable().
    * 
    * @param clazz Class to be examined
    * @param injectorFactory the InjectorFactory used for clazz
    * @return true if and only if validation is turned on for clazz
    */
    boolean isValidatable(Class<?> clazz, InjectorFactory injectorFactory);
   
   /**
    * Indicates if validation is turned on for a class.
    * This method should be called only from the resteasy-cdi module.
    * 
    * @param clazz Class to be examined
    * @return true if and only if validation is turned on for clazz
    */
   boolean isValidatableFromCDI(Class<?> clazz);
  
   /**
    * Throws a ResteasyViolationException if any validation violations have been detected.
    * The method should be called only from the resteasy-cdi module.
    * @param request http request
    */
   void checkViolationsfromCDI(HttpRequest request);
   
   /**
    * Throws a ResteasyViolationException if either a ConstraintViolationException or a
    * ResteasyConstraintViolationException is embedded in the cause hierarchy of e.
    * 
    * @param request http request
    * @param e exception
    */
   void checkForConstraintViolations(HttpRequest request, Exception e);
}