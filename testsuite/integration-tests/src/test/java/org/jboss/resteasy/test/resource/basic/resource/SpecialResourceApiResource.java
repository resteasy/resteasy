package org.jboss.resteasy.test.resource.basic.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

@Path("/{api:(?i:api)}")
public class SpecialResourceApiResource {
   private static Logger logger = Logger.getLogger(SpecialResourceApiResource.class);

   @Path("/{func:(?i:func)}")
   @GET
   @Produces("text/plain")
   public String func() {
      return "hello";
   }

   @PUT
   public void put(@Context HttpHeaders headers, String val) {
      logger.info(headers.getMediaType());
      Assert.assertEquals("Wrong request content", val, "hello");
   }
}
