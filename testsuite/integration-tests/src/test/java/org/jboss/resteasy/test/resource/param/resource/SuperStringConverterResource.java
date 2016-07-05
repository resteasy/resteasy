package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public class SuperStringConverterResource {
    @Path("person/{person}")
    @PUT
    public void put(@PathParam("person") SuperStringConverterPerson p) {
        Assert.assertEquals(p.getName(), "name");
    }

    @Path("company/{company}")
    @PUT
    public void putCompany(@PathParam("company") SuperStringConverterCompany c) {
        Assert.assertEquals(c.getName(), "name");
    }
}
