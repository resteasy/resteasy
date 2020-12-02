package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/test")
public class LazyInitUriInfoInjectionResource {
   private static Logger logger = Logger.getLogger(LazyInitUriInfoInjectionResource.class);

   private UriInfo info;

   @Context
   public void setUriInfo(UriInfo i) {
      this.info = i;
      logger.info(i.getClass().getName());
   }

   @GET
   @Produces("text/plain")
   public String get() {
      String val = info.getQueryParameters().getFirst("h");
      if (val == null) {
         val = "";
      }
      return val;
   }


}
