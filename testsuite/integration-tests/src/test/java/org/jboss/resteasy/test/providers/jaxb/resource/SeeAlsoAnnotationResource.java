package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;

@Path("/see")
public class SeeAlsoAnnotationResource {
    @Path("/intf")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void put(SeeAlsoAnnotationFooIntf foo) {
        Assertions.assertTrue(foo instanceof SeeAlsoAnnotationRealFoo, "The input parameter for the resource has wrong type");
        Assertions.assertEquals(((SeeAlsoAnnotationRealFoo) foo).getName(), "bill", "The foo object has unexpected content");
    }

    @Path("base")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void put(SeeAlsoAnnotationBaseFoo foo) {
        Assertions.assertTrue(foo instanceof SeeAlsoAnnotationRealFoo, "The input parameter for the resource has wrong type");
        Assertions.assertEquals(((SeeAlsoAnnotationRealFoo) foo).getName(), "bill", "The foo object has unexpected content");
    }
}
