package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.test.client.SyncInvokeTest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/test")
public class SyncInvokeResource {
    @GET
    @Produces("text/plain")
    public String get() {
        return "get";
    }

    @PUT
    @Consumes("text/plain")
    public String put(String str) {
        return "put " + str;
    }

    @POST
    @Consumes("text/plain")
    public String post(String str) {
        return "post " + str;
    }

    @DELETE
    @Produces("text/plain")
    public String delete() {
        return "delete";
    }

    @SyncInvokeTest.PATCH
    @Produces("text/plain")
    @Consumes("text/plain")
    public String patch(String str) {
        return "patch " + str;
    }
}
