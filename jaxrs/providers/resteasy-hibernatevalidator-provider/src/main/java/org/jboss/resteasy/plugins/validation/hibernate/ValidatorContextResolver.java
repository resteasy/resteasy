package org.jboss.resteasy.plugins.validation.hibernate;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.validation.GeneralValidator;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 */
@Provider
public class ValidatorContextResolver implements ContextResolver<GeneralValidator>
{
   private final static Logger logger = Logger.getLogger(ValidatorContextResolver.class);
   private static volatile GeneralValidator generalValidator;
   final static Object RD_LOCK = new Object();

   // this used to be initialized in a static block, but I was having trouble class loading the context resolver in some
   // environments.  So instead of failing and logging a warning when the resolver is instantiated at deploy time
   // we log any validation warning when trying to obtain the validator.
   static GeneralValidator getGeneralValidator()
   {
      GeneralValidator tmpValidator = generalValidator;
      if (tmpValidator == null)
      {
         synchronized (RD_LOCK)
         {
            tmpValidator = generalValidator;
            if (generalValidator == null)
            {
               HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
               Validator validator = config.buildValidatorFactory().getValidator();
               MethodValidator methodValidator = validator.unwrap(MethodValidator.class);
               generalValidator = tmpValidator = new GeneralValidatorImpl(validator, methodValidator);

            }
         }
      }
      return generalValidator;
   }

   @Override
   public GeneralValidator getContext(Class<?> type) {
      try
      {
         return getGeneralValidator();
      }
      catch (Exception e)
      {
         logger.warn("Unable to load Validation support", e);
      }
      return null;
   }
}
