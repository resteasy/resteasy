package org.jboss.resteasy.plugins.validation.hibernate;

import org.jboss.resteasy.spi.validation.ValidatorAdapter;

import javax.validation.Validation;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class HibernateValidatorContextResolver implements
		ContextResolver<ValidatorAdapter> {

   @Override
   public ValidatorAdapter getContext(Class<?> type)
   {
      // TODO Auto-generated method stub
      return null;
   }

//	private static final HibernateValidatorAdapter adapter = new HibernateValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
//	
//	@Override
//	public ValidatorAdapter getContext(Class<?> type) {
//		return adapter; 
//	}

}
