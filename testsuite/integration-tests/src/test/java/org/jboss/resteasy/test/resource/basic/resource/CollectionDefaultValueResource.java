package org.jboss.resteasy.test.resource.basic.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/collection")
public class CollectionDefaultValueResource {
    @GET
    @Produces("text/plain")
    public String get(@QueryParam("nada") List<String> params) {
        Assertions.assertNotNull(params, "Empty list was sent like null");
        Assertions.assertEquals(0, params.size(), "Empty list was sent not empty");
        return "hello";
    }

}
