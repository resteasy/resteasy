package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/test")
public class ExceptionMapperJaxbResource {
   @POST
   @Consumes("application/xml")
   public void post(AbstractJaxbClassPerson person) {
   }
}
