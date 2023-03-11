package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.Assert;

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
