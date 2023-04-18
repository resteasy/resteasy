package org.jboss.resteasy.test.resource.basic.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

import org.junit.Assert;

@Path("/delete")
public class SpecialResourceDeleteResource {
    @DELETE
    @Consumes("text/plain")
    public void delete(String msg) {
        Assert.assertEquals("Wrong request content", "hello", msg);
    }
}
