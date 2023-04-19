package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public interface ResponseObjectClientIntf {
    @GET
    ResponseObjectBasicObjectIntf get();

    @GET
    @Path("link-header")
    ResponseObjectHateoasObject performGetBasedOnHeader();
}
