package org.jboss.resteasy.test.providers.sse;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Priority(Priorities.USER-200)
public class CustomJacksonProvider extends ResteasyJackson2Provider {

   public CustomJacksonProvider() {
      super();
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      this.setMapper(mapper);
   }
 }
