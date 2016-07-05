package org.jboss.resteasy.test.providers.jettison.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.messagebody.WriterUtility;
import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.List;

@Path("/")
public class JsonCollectionMyResource {

    protected static final Logger logger = Logger.getLogger(JsonCollectionMyResource.class.getName());

    @Path("/array")
    @Produces("application/json;charset=UTF-8")
    @GET
    public JsonCollectionFoo[] get() throws Exception {
        JsonCollectionFoo[] foo = {new JsonCollectionFoo("bill{"), new JsonCollectionFoo("monica\"}")};
        logger.info("START");
        logger.info(WriterUtility.asString(foo, "application/json"));
        logger.info("FINISH");
        return foo;
    }

    @Path("/array")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public JsonCollectionFoo[] array(JsonCollectionFoo[] foo) {
        Assert.assertEquals("The size of the array is not the expected one", 2, foo.length);
        Assert.assertEquals("The first element of the array is not the expected one", foo[0].getTest(), "bill{");
        Assert.assertEquals("The second element of the array is not the expected one", foo[1].getTest(), "monica\"}");
        return foo;
    }

    @Path("/list")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public List<JsonCollectionFoo> list(List<JsonCollectionFoo> foo) {
        logger.info("POST LIST");
        Assert.assertEquals("The length of the list is not the expected one", 2, foo.size());
        Assert.assertEquals("The first element of the list is not the expected one", foo.get(0).getTest(), "bill{");
        Assert.assertEquals("The second element of the list is not the expected one", foo.get(1).getTest(), "monica\"}");
        return foo;
    }


    @Path("/empty/array")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public JsonCollectionFoo[] emptyArray(JsonCollectionFoo[] foo) {
        Assert.assertEquals("The size of the array is not the expected one", 0, foo.length);
        return foo;
    }

    @Path("/empty/list")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public List<JsonCollectionFoo> emptyList(List<JsonCollectionFoo> foo) {
        Assert.assertEquals("The length of the list is not the expected one", 0, foo.size());
        return foo;
    }
}
