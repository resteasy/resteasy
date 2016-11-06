package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/namespaced")
public class JaxbCollectionNamespacedResource {
    @Path("/array")
    @Produces("application/xml")
    @Consumes("application/xml")
    @POST
    public JaxbCollectionNamespacedFoo[] naked(JaxbCollectionNamespacedFoo[] foo) {
        Assert.assertEquals("The unmarshalled array doesn't contain 1 item, which is expected", 1, foo.length);
        Assert.assertEquals("The unmarshalled array doesn't contain correct element value", foo[0].getTest(), "hello");
        return foo;
    }

    @Path("/list")
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Wrapped(element = "list", namespace = "", prefix = "")
    public List<JaxbCollectionNamespacedFoo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<JaxbCollectionNamespacedFoo> list) {
        Assert.assertEquals("The unmarshalled list doesn't contain 1 item, which is expected", 1, list.size());
        Assert.assertEquals("The unmarshalled list doesn't contain correct element value", list.get(0).getTest(), "hello");
        return list;
    }


}
