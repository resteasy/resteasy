package org.jboss.resteasy.plugins.validation.hibernate;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.plugins.validation.hibernate.i18n.LogMessages;
import org.jboss.resteasy.plugins.validation.hibernate.i18n.Messages;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 * 
 * @deprecated Use resteasy-validator-provider-11.
 */
@Deprecated
public class AbstractValidatorContextResolver
{
   final Object RD_LOCK = new Object();
   private volatile ValidatorFactory validatorFactory;

   // this used to be initialized in a static block, but I was having trouble class loading the context resolver in some
   // environments.  So instead of failing and logging a warning when the resolver is instantiated at deploy time
   // we log any validation warning when trying to obtain the validator.
   protected GeneralValidatorCDI getGeneralValidator()
   {
      ValidatorFactory tmpValidatorFactory = validatorFactory;
      if (tmpValidatorFactory == null)
      {
         synchronized (RD_LOCK)
         {
            tmpValidatorFactory = validatorFactory;
            if (validatorFactory == null)
            {
               HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
               tmpValidatorFactory = validatorFactory = config.buildValidatorFactory();

            }
         }
      }
      Validator validator = validatorFactory.getValidator();
      MethodValidator methodValidator = validator.unwrap(MethodValidator.class);
      return  new GeneralValidatorImpl(validator, methodValidator);
   }

   public GeneralValidatorCDI getContext(Class<?> type) {
      try
      {
         return getGeneralValidator();
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.warn(Messages.MESSAGES.unableToLoadValidationSupport(), e);
      }
      return null;
   }
}
