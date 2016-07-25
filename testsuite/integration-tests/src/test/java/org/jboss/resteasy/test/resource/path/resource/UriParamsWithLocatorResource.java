package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class UriParamsWithLocatorResource {
    @GET
    @Path("/{id}")
    public String get(@PathParam("id") String id) {
        Assert.assertEquals("2", id);
        return id;
    }
}
