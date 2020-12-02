package org.jboss.resteasy.test.spring.deployment.resource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * This class provides a web-based facade for an injected service.
 */
@Path("/")
public class JavaConfigResource {

   private static Logger logger = Logger.getLogger(JavaConfigResource.class);
   JavaConfigService service;

   @Autowired
   public void setService(JavaConfigService service) {
      logger.info("*** service injected=" + service);
      this.service = service;
   }

   public JavaConfigResource() {
      logger.info("*** resource created:" + super.toString());
   }


   @GET
   @Path("invoke")
   @Produces(MediaType.TEXT_PLAIN)
   public String invoke() {
      return service.invoke();
   }
}
