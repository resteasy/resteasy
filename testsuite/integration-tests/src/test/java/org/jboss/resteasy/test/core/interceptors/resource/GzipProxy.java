package org.jboss.resteasy.test.core.interceptors.resource;


import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/gzippost")
    public interface GzipProxy {

        @Consumes("application/json")
        @POST
        @GZIP
        public Response post(@GZIP Pair pair);
    }
