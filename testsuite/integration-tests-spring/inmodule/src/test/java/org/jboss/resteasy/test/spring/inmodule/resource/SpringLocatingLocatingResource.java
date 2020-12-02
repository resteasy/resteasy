package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.Path;

@Path("/")
public class SpringLocatingLocatingResource {

   private static Logger logger = Logger.getLogger(SpringLocatingLocatingResource.class.getName());

   @Path("locating")
   public SpringLocatingSimpleResource getLocating() {
      logger.info("LOCATING...");
      return new SpringLocatingSimpleResource();
   }
}
