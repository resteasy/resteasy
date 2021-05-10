package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Component
@Path("/resteasy828")
public class RESTEasy828Resource {
   @GET
   public String get(@Context ServletContext context) {
      return context.getClass().toString();
   }
}
