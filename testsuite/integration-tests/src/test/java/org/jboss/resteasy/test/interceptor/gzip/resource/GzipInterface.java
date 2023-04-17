package org.jboss.resteasy.test.interceptor.gzip.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.GZIP;

@Path("/gzip")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GzipInterface {

    @GET
    @Path("/process")
    @GZIP
    String process(@QueryParam("name") String message);

}
