package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.junit.Assert;

@Path("/")
public class UriParamsWithLocatorLocator {
    @Path("/{id}")
    public UriParamsWithLocatorResource get(@PathParam("id") String id) {
        Assert.assertEquals("1", id);
        return new UriParamsWithLocatorResource();

    }
}
