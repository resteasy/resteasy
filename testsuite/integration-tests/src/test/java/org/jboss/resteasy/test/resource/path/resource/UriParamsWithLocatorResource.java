package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.Assert;

public class UriParamsWithLocatorResource {
    @GET
    @Path("/{id}")
    public String get(@PathParam("id") String id) {
        Assert.assertEquals("2", id);
        return id;
    }
}
