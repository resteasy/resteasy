package org.jboss.resteasy.test.interceptor.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AddDynamicFeature implements DynamicFeature {

   private static final Logger LOG = Logger.getLogger(AddDynamicFeature.class.getName());

   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
      if (resourceInfo.getResourceMethod().getName().equals("hello")) {
         context.register(GreetingInterceptor.class);
         LOG.info("This should be happening exactly once");
      }
   }

}
