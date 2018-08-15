package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
