package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("")
public class ValidationHibernateI18NResource {
   @GET
   @Path("test")
   @Size(min = 2)
   public String test() {
      return "a";
   }
}
