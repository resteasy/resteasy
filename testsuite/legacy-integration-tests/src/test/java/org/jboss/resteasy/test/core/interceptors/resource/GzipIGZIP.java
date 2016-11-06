package org.jboss.resteasy.test.core.interceptors.resource;

import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
