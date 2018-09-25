package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("refresh")
@Component
public class ContextRefreshResource {
   @Path("locator/{id}")
   @Produces("text/plain")
   public String locator() {
      return "locator";
   }
}
