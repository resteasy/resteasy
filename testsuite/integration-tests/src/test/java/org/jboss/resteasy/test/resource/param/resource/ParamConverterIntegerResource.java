package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class ParamConverterIntegerResource {
    @Path("{pojo}")
    @PUT
    public void put(@QueryParam("pojo") int q, @PathParam("pojo") int pp, @MatrixParam("pojo") int mp,
            @HeaderParam("pojo") int hp) {
        Assertions.assertEquals(44, q);
        Assertions.assertEquals(44, pp);
        Assertions.assertEquals(44, mp);
        Assertions.assertEquals(44, hp);
    }
}
