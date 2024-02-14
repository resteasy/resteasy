package org.jboss.resteasy.test.core.basic.resource;

import java.util.Stack;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.InternalDispatcher;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.test.core.basic.InternalDispatcherTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class InternalDispatcherForwardingResource {
    public Stack<String> uriStack = new Stack<String>();
    String basic = "basic";
    @Context
    UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    @Path("/basic")
    public String getBasic() {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        return basic;
    }

    @GET
    @Produces("text/plain")
    @Path("/forward/basic")
    public String forwardBasic(@Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        return (String) dispatcher.getEntity("/basic");
    }

    @PUT
    @POST
    @Consumes("text/plain")
    @Path("/basic")
    public void putBasic(String basic) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        this.basic = basic;
    }

    @DELETE
    @Path("/basic")
    public void deleteBasic() {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        this.basic = "basic";
    }

    @PUT
    @Consumes("text/plain")
    @Path("/forward/basic")
    public void putForwardBasic(String basic,
            @Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        dispatcher.putEntity("/basic", basic);
    }

    @POST
    @Consumes("text/plain")
    @Path("/forward/basic")
    public void postForwardBasic(String basic,
            @Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        dispatcher.postEntity("/basic", basic);
    }

    @DELETE
    @Path("/forward/basic")
    public void deleteForwardBasic(@Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        dispatcher.delete("/basic");
    }

    @GET
    @Produces("text/plain")
    @Path("/object/{id}")
    public Response getObject(@PathParam("id") Integer id) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        if (id == 0) {
            return Response.noContent().build();
        } else {
            return Response.ok("object" + id).build();
        }
    }

    @GET
    @Path("/forward/object/{id}")
    public Response forwardObject(@PathParam("id") Integer id,
            @Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        return dispatcher.getResponse("/object/" + id);
    }

    @GET
    @Path("/infinite-forward")
    @Produces("text/plain")
    public int infinitFoward(@Context InternalDispatcher dispatcher,
            @QueryParam("count") @DefaultValue("0") int count) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        try {
            dispatcher.getEntity("/infinite-forward?count=" + (count + 1));
            // we'll never reach 20, since the max count of times through the
            // system is 20, and first time through is 0
            Assertions.assertNotSame(20, count);
        } catch (BadRequestException e) {

        } finally {
            Assertions.assertEquals(count, MessageBodyParameterInjector.bodyCount());
            Assertions.assertEquals(count + 1, ResteasyContext.getContextDataLevelCount());
        }
        return ResteasyContext.getContextDataLevelCount();
    }

    @GET
    @Path(InternalDispatcherTest.PATH + "/basic")
    @Produces("text/plain")
    public String getComplexBasic() {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        return InternalDispatcherTest.PATH + basic;
    }

    @GET
    @Produces("text/plain")
    @Path(InternalDispatcherTest.PATH + "/forward/basic")
    public String complexForwardBasic(@Context InternalDispatcher dispatcher) {
        uriStack.push(uriInfo.getAbsolutePath().toString());
        return (String) dispatcher.getEntity(InternalDispatcherTest.PATH + "/basic");
    }
}
