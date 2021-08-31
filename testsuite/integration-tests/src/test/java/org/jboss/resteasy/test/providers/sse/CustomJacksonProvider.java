package org.jboss.resteasy.test.providers.sse;

import javax.annotation.Priority;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Priority(Priorities.USER-200)
public class CustomJacksonProvider extends ResteasyJackson2Provider {

   public CustomJacksonProvider()
   {
      super();
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      this.setMapper(mapper);
   }
}
