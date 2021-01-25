package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Component
@Path("/resteasy828")
public class RESTEasy828Resource {
   @GET
   public String get(@Context ServletContext context) {
      return context.getClass().toString();
   }
}
