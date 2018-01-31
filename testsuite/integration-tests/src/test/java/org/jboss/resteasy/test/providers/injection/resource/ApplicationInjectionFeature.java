package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class ApplicationInjectionFeature implements Feature {

   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public boolean configure(FeatureContext context) {
      context.register(new ApplicationInjectionFeatureFilter(application));
      return true;
   }

}
