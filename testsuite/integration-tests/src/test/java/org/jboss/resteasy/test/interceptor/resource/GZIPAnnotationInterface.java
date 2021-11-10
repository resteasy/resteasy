package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.GZIP;

@Path("")
public interface GZIPAnnotationInterface {

   @Path("/foo")
   @Consumes("text/plain")
   @Produces("text/plain")
   @GZIP
   @POST
   String getFoo(@GZIP String request);
}
