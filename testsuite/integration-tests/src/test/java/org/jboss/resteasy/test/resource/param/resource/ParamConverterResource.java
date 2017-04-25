package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public class ParamConverterResource {
    @Path("{pojo}")
    @PUT
    public void put(@QueryParam("pojo") ParamConverterPOJO q, @PathParam("pojo") ParamConverterPOJO pp, @MatrixParam("pojo") ParamConverterPOJO mp,
                    @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assert.assertEquals(q.getName(), "pojo");
        Assert.assertEquals(pp.getName(), "pojo");
        Assert.assertEquals(mp.getName(), "pojo");
        Assert.assertEquals(hp.getName(), "pojo");
    }
}
