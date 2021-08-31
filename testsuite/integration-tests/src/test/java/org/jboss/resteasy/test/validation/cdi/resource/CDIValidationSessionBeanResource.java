package org.jboss.resteasy.test.validation.cdi.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("test")
@Stateful
public class CDIValidationSessionBeanResource implements CDIValidationSessionBeanProxy {
   private static final Logger log = LoggerFactory.getLogger(CDIValidationSessionBeanResource.class);

   @GET
   @Path("resource/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   public int test(@PathParam("param") int param) {
      log.info("entering CDIValidationSessionBeanResource.test()");
      return param;
   }
}
