package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class JAXRS21SyncInvokeResource {
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

    @PATCH
    @Produces("text/plain")
    @Consumes("text/plain")
    public String patch(String str) {
        return "patch " + str;
    }
}
