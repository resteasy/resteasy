package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.junit.jupiter.api.Assertions;

@Path("/namespaced")
public class JaxbCollectionNamespacedResource {
    @Path("/array")
    @Produces("application/xml")
    @Consumes("application/xml")
    @POST
    public JaxbCollectionNamespacedFoo[] naked(JaxbCollectionNamespacedFoo[] foo) {
        Assertions.assertEquals(1, foo.length,
                "The unmarshalled array doesn't contain 1 item, which is expected");
        Assertions.assertEquals(foo[0].getTest(), "hello",
                "The unmarshalled array doesn't contain correct element value");
        return foo;
    }

    @Path("/list")
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Wrapped(element = "list", namespace = "", prefix = "")
    public List<JaxbCollectionNamespacedFoo> wrapped(
            @Wrapped(element = "list", namespace = "", prefix = "") List<JaxbCollectionNamespacedFoo> list) {
        Assertions.assertEquals(1, list.size(),
                "The unmarshalled list doesn't contain 1 item, which is expected");
        Assertions.assertEquals(list.get(0).getTest(), "hello",
                "The unmarshalled list doesn't contain correct element value");
        return list;
    }

}
