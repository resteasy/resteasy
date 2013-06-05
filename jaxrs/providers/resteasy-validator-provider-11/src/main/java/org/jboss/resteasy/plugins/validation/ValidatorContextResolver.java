package org.jboss.resteasy.plugins.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.jboss.resteasy.plugins.providers.validation.GeneralValidator;

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
   private static final GeneralValidator generalValidator;

   static
   {
      HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
      Validator validator = config.buildValidatorFactory().getValidator();
      generalValidator = new GeneralValidatorImpl(validator);
   }

   @Override
   public GeneralValidator getContext(Class<?> type) {
      return generalValidator;
   }
}
