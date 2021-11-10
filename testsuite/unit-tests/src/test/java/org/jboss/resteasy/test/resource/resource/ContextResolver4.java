package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ContextResolver4 implements ContextResolver<String> {
   public String getContext(Class<?> type) {
      if (type.equals(float.class)) {
         return "4";
      }
      return null;
   }
}
