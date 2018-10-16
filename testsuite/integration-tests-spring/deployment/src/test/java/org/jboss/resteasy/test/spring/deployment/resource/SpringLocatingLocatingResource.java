package org.jboss.resteasy.test.spring.deployment.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Path;

@Path("/")
public class SpringLocatingLocatingResource {

   private static Logger logger = Logger.getLogger(SpringLocatingLocatingResource.class.getName());

   @Path("locating")
   public SpringLocatingSimpleResource getLocating() {
      logger.info("LOCATING...");
      return new SpringLocatingSimpleResource();
   }
}
