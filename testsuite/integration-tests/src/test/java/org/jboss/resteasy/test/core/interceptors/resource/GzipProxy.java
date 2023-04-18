package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

@Path("/gzippost")
public interface GzipProxy {

    @Consumes("application/json")
    @POST
    @GZIP
    Response post(@GZIP Pair pair);
}
