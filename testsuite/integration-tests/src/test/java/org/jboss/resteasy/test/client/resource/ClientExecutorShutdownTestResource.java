package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/test")
public class ClientExecutorShutdownTestResource {

   private static final Logger logger = Logger.getLogger(ClientExecutorShutdownTestResource.class);

   @POST
   public void post() {
      logger.info("In POST");
   }
}
