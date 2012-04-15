package org.jboss.resteasy.plugins.providers.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.spi.validation.GeneralValidator;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 *
 * @version $Revision: 1.1 $
 * Created Mar 7, 2012
 */
@Provider
public class DefaultHibernateValidatorContextResolver implements ContextResolver<GeneralValidator>
{
   private static final GeneralValidator generalValidator;
   
   static
   {
      HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
      Validator validator = config.buildValidatorFactory().getValidator();
      MethodValidator methodValidator = null;
      if (validator instanceof MethodValidator)
      {
         methodValidator = MethodValidator.class.cast(validator);
      }
      else
      {
         config = Validation.byProvider(HibernateValidator.class).configure();
         methodValidator = config.buildValidatorFactory().getValidator().unwrap(MethodValidator.class); 
      }
      generalValidator = new GeneralValidatorImpl(validator, methodValidator); 
   }

   @Override
   public GeneralValidator getContext(Class<?> type) {
      return generalValidator; 
   }
}
