package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AddFeature implements Feature {

   public static final String PROPERTY = "FeatureProperty";
   public static final String PROPERTY_VALUE = "FeaturePropertyValue";

   public static final class DoNothingGlobalRequestFilter implements ContainerRequestFilter {

      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException {
      }

   }

   @Override
   public boolean configure(FeatureContext context) {
         context.property(PROPERTY, PROPERTY_VALUE);
         context.register(DoNothingGlobalRequestFilter.class);
         return true;
   }

}
