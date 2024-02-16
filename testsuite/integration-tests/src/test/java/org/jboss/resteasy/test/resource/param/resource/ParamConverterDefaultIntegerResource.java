package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class ParamConverterDefaultIntegerResource {
    @PUT
    public void putDefault(@QueryParam("pojo") @DefaultValue("100") int q,
            @MatrixParam("pojo") @DefaultValue("100") int mp, @DefaultValue("100") @HeaderParam("pojo") int hp) {
        Assertions.assertEquals(100100, q);
        Assertions.assertEquals(100100, mp);
        Assertions.assertEquals(100100, hp);
    }
}
