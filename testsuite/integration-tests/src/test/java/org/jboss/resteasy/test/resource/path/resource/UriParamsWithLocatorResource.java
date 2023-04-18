package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.junit.Assert;

public class UriParamsWithLocatorResource {
    @GET
    @Path("/{id}")
    public String get(@PathParam("id") String id) {
        Assert.assertEquals("2", id);
        return id;
    }
}
