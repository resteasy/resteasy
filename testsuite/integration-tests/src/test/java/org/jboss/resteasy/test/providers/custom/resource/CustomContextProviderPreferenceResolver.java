package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.validation.AbstractValidatorContextResolver;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;

@Provider
public class CustomContextProviderPreferenceResolver extends AbstractValidatorContextResolver implements ContextResolver<GeneralValidator>
{
   public static boolean entered = false;

   public GeneralValidatorCDI getContext(Class<?> type)
   {
      entered = true;
      return super.getContext(type);
   }
}
