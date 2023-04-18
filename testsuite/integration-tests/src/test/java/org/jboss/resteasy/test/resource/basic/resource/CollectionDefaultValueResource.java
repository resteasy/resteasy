package org.jboss.resteasy.test.resource.basic.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.junit.Assert;

@Path("/collection")
public class CollectionDefaultValueResource {
    @GET
    @Produces("text/plain")
    public String get(@QueryParam("nada") List<String> params) {
        Assert.assertNotNull("Empty list was sent like null", params);
        Assert.assertEquals("Empty list was sent not empty", 0, params.size());
        return "hello";
    }

}
