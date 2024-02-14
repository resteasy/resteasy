package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class ParamConverterDefaultResource {
    @PUT
    public void putDefault(@QueryParam("pojo") @DefaultValue("default") ParamConverterPOJO q,
            @MatrixParam("pojo") @DefaultValue("default") ParamConverterPOJO mp,
            @DefaultValue("default") @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assertions.assertEquals(q.getName(), "default");
        Assertions.assertEquals(mp.getName(), "default");
        Assertions.assertEquals(hp.getName(), "default");
    }
}
