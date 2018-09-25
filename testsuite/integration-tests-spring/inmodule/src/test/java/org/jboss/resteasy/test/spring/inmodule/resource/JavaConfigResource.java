package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
