package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public class ClientErrorBadMediaTypeResource {
    @Consumes("application/bar")
    @Produces("application/foo")
    @POST
    public String doPost(String entity) {
        return "content";
    }

    @Produces("text/plain")
    @GET
    @Path("complex/match")
    public String get() {
        return "content";
    }

    @Produces("text/xml")
    @GET
    @Path("complex/{uriparam: [^/]+}")
    public String getXml(@PathParam("uriparam") String param) {
        return "<" + param + "/>";
    }

    @DELETE
    public void delete() {
    }

    @Path("/nocontent")
    @POST
    public void noreturn(String entity) {
    }
}
