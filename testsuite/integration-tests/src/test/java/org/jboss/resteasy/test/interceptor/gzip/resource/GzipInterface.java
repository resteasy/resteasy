package org.jboss.resteasy.test.interceptor.gzip.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
