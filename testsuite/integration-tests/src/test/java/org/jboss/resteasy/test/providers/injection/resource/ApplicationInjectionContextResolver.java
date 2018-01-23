package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationInjectionContextResolver implements ContextResolver<String> {

   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public String getContext(Class<?> type) {
      return getClass() + ":" + application.getName();
   }
}
