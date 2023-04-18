package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.junit.Assert;

@Path("/")
public class ParamConverterDefaultResource {
    @PUT
    public void putDefault(@QueryParam("pojo") @DefaultValue("default") ParamConverterPOJO q,
            @MatrixParam("pojo") @DefaultValue("default") ParamConverterPOJO mp,
            @DefaultValue("default") @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assert.assertEquals(q.getName(), "default");
        Assert.assertEquals(mp.getName(), "default");
        Assert.assertEquals(hp.getName(), "default");
    }
}
