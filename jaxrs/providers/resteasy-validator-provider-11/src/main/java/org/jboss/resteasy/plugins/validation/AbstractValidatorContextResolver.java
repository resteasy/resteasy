package org.jboss.resteasy.plugins.validation;

import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.BootstrapConfiguration;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableType;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.jboss.resteasy.plugins.validation.i18n.LogMessages;
import org.jboss.resteasy.plugins.validation.i18n.Messages;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 */
public class AbstractValidatorContextResolver
{
   private volatile ValidatorFactory validatorFactory;
   final static Object RD_LOCK = new Object();
   private volatile Configuration<?> config;
   private volatile BootstrapConfiguration bootstrapConfiguration;

   // this used to be initialized in a static block, but I was having trouble class loading the context resolver in some
   // environments.  So instead of failing and logging a warning when the resolver is instantiated at deploy time
   // we log any validation warning when trying to obtain the ValidatorFactory. 
   ValidatorFactory getValidatorFactory()
   {
      ValidatorFactory tmpValidatorFactory = validatorFactory;
      if (tmpValidatorFactory == null)
      {
         synchronized (RD_LOCK)
         {
            tmpValidatorFactory = validatorFactory;
            if (tmpValidatorFactory == null)
            {
               try
               {
                  // Also look up java:comp/env
                  Context context = new InitialContext();
                  validatorFactory = tmpValidatorFactory = ValidatorFactory.class.cast(context.lookup("java:comp/ValidatorFactory"));
                  LogMessages.LOGGER.debug(Messages.MESSAGES.usingValidatorFactorySupportsCDI(validatorFactory));
               }
               catch (NamingException e)
               {
                  LogMessages.LOGGER.info(Messages.MESSAGES.usingValidatorFactoryDoesNotSupportCDI());
                  HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
                  validatorFactory = tmpValidatorFactory = config.buildValidatorFactory();
               }
            }
         }
      }
      return validatorFactory;
   }

   BootstrapConfiguration getConfig()
   {
       BootstrapConfiguration tmpConfig = bootstrapConfiguration;
       if (tmpConfig == null)
       {
          synchronized (RD_LOCK)
          {
             tmpConfig = bootstrapConfiguration;
             if (tmpConfig == null)
             {
                 config = Validation.byDefaultProvider().configure();
                 bootstrapConfiguration = tmpConfig = config.getBootstrapConfiguration();

             }
          }
       }
       return tmpConfig;
   }

   public GeneralValidatorCDI getContext(Class<?> type) {
      try
      {
         BootstrapConfiguration bootstrapConfiguration = getConfig();
         boolean isExecutableValidationEnabled = bootstrapConfiguration.isExecutableValidationEnabled();
         Set<ExecutableType> defaultValidatedExecutableTypes = bootstrapConfiguration.getDefaultValidatedExecutableTypes();
         return new GeneralValidatorImpl(getValidatorFactory(), isExecutableValidationEnabled, defaultValidatedExecutableTypes);
      }
      catch (Exception e)
      {
         throw new ValidationException(Messages.MESSAGES.unableToLoadValidationSupport(), e);
      }
   }
}
