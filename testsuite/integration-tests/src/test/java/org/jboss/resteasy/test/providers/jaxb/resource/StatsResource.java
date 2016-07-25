package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

@Path("/")
public class StatsResource {
    @Path("locator")
    public Object getLocator() {
        return null;
    }

    @Path("entry/{foo:.*}")
    @PUT
    @Produces("text/xml")
    @Consumes("application/json")
    public void put() {

    }

    @Path("entry/{foo:.*}")
    @POST
    @Produces("text/xml")
    @Consumes("application/json")
    public void post() {

    }

    @DELETE
    @Path("resource")
    public void delete() {
    }

    @HEAD
    @Path("resource")
    public void head() {
    }
}
