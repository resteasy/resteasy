package org.jboss.resteasy.plugins.providers.validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 *
 * @version $Revision: 1.1 $
 * Created Mar 7, 2012
 */
@Provider
public class DefaultHibernateValidatorContextResolver implements ContextResolver<Validator>
{
   private static final Validator generalValidator;

   static
   {
      HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
      generalValidator = config.buildValidatorFactory().getValidator();
   }

   @Override
   public Validator getContext(Class<?> type) {
      return generalValidator;
   }
}
