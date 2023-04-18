package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

@Path("/gzippost")
public interface GzipProxy {

    @Consumes("application/json")
    @POST
    @GZIP
    Response post(@GZIP Pair pair);
}
