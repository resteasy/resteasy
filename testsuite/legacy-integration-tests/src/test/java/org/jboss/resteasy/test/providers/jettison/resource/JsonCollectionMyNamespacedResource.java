package org.jboss.resteasy.test.providers.jettison.resource;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.List;

@Path("/namespaced")
public class JsonCollectionMyNamespacedResource {
    @Path("/array")
    @Produces("application/json;charset=UTF-8")
    @GET
    @Mapped(namespaceMap = @XmlNsMap(namespace = "http://foo.com", jsonName = "foo.com"))
    public JsonCollectionNamespacedFoo[] get() throws Exception {
        JsonCollectionNamespacedFoo[] foo = {new JsonCollectionNamespacedFoo("bill{"), new JsonCollectionNamespacedFoo("monica\"}")};
        return foo;
    }

    @Path("/array")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public JsonCollectionNamespacedFoo[] array(JsonCollectionNamespacedFoo[] foo) {
        Assert.assertEquals("The size of the array is not the expected one", 2, foo.length);
        Assert.assertEquals("The first element of the array is not the expected one", foo[0].getTest(), "bill{");
        Assert.assertEquals("The second element of the array is not the expected one", foo[1].getTest(), "monica\"}");
        return foo;
    }

    @Path("/list")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public List<JsonCollectionNamespacedFoo> list(List<JsonCollectionNamespacedFoo> foo) {
        Assert.assertEquals("The length of the list is not the expected one", 2, foo.size());
        Assert.assertEquals("The first element of the list is not the expected one", foo.get(0).getTest(), "bill{");
        Assert.assertEquals("The second element of the list is not the expected one", foo.get(1).getTest(), "monica\"}");
        return foo;
    }


    @Path("/empty/array")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public JsonCollectionNamespacedFoo[] emptyArray(JsonCollectionNamespacedFoo[] foo) {
        Assert.assertEquals("The size of the array is not the expected one", 0, foo.length);
        return foo;
    }

    @Path("/empty/list")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public List<JsonCollectionNamespacedFoo> emptyList(List<JsonCollectionNamespacedFoo> foo) {
        Assert.assertEquals("The length of the list is not the expected one", 0, foo.size());
        return foo;
    }
}
