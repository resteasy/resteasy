package org.jboss.resteasy.test.core.interceptors.resource;


import org.jboss.resteasy.annotations.GZIP;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/gzippost")
public interface GzipProxy {

   @Consumes("application/json")
   @POST
   @GZIP
   Response post(@GZIP Pair pair);
}
