package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("text/*")
public class ContextResolver6 implements ContextResolver<String> {
   public String getContext(Class<?> type) {
      if (type.equals(float.class)) {
         return "6";
      }
      return null;
   }
}
