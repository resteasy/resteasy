package org.jboss.resteasy.test.core.interceptors.resource;

import org.jboss.resteasy.annotations.GZIP;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public interface GzipIGZIP {
   @GET
   @Path("text")
   @Produces("text/plain")
   String getText();

   @GET
   @Path("encoded/text")
   @GZIP
   String getGzipText();

   @GET
   @Path("encoded/text/error")
   @GZIP
   String getGzipErrorText();

}
