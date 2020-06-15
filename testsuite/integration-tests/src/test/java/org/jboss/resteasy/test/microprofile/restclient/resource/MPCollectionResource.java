package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/thePatron")
@ApplicationScoped
public class MPCollectionResource {
    @Inject
    @RestClient
    MPCollectionServiceIntf service;

    @GET
    @Path("got")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> doTheGet() {
        List<String> l = service.getList();
        l.add("thePatron");
        return l;
    }

    @GET
    @Path("checking")
    public String pong() {
        if (service == null) {
            return "null thePatron";
        }
        return service.ping() + " thePatron";
    }
}
