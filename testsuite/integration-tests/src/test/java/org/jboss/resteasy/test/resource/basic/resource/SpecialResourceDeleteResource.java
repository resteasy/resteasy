package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;

import org.junit.Assert;

@Path("/delete")
public class SpecialResourceDeleteResource {
    @DELETE
    @Consumes("text/plain")
    public void delete(String msg) {
        Assert.assertEquals("Wrong request content", "hello", msg);
    }
}
