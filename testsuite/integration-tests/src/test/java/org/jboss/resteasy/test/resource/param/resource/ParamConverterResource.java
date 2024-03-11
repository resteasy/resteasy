package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class ParamConverterResource {
    @Path("{pojo}")
    @PUT
    public void put(@QueryParam("pojo") ParamConverterPOJO q, @PathParam("pojo") ParamConverterPOJO pp,
            @MatrixParam("pojo") ParamConverterPOJO mp,
            @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assertions.assertEquals(q.getName(), "pojo");
        Assertions.assertEquals(pp.getName(), "pojo");
        Assertions.assertEquals(mp.getName(), "pojo");
        Assertions.assertEquals(hp.getName(), "pojo");
    }
}
