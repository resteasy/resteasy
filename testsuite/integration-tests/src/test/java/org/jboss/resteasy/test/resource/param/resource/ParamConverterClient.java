package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/")
public interface ParamConverterClient {
   @Path("{pojo}")
   @PUT
   void put(@QueryParam("pojo") String q, @PathParam("pojo") String pp, @MatrixParam("pojo") String mp,
            @HeaderParam("pojo") String hp);
}
