package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ContextResolver1 implements ContextResolver<String> {
   public String getContext(Class<?> type) {
      if (type.equals(int.class)) {
         return "1";
      }
      return null;
   }
}
