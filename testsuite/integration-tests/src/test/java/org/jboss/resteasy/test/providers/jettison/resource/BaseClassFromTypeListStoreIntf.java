package org.jboss.resteasy.test.providers.jettison.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.util.List;

public interface BaseClassFromTypeListStoreIntf<T> {
    @GET
    @Path("/intf")
    @Produces("application/json")
    @BadgerFish
    @Wrapped
    List<T> list();

    @PUT
    @Path("/intf")
    @Consumes("application/json")
    void put(@Wrapped @BadgerFish List<T> list);
}
