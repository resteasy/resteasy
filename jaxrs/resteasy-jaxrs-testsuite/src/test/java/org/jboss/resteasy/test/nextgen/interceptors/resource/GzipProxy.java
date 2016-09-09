package org.jboss.resteasy.test.nextgen.interceptors.resource;

import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/gzippost")
public interface GzipProxy {

    @Consumes(MediaType.TEXT_PLAIN)
    @POST
    @GZIP
    public Response post(@GZIP Pair pair);
}
