package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("base/resource")
public class SetRequestUriResource {

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("setrequesturi1/uri")
    public String setRequestUri() {
        return "OK";
    }

    @GET
    @Path("setrequesturi1")
    public String setRequestUriDidNotChangeUri() {
        return "Filter did not change the uri to go to";
    }

    @GET
    @Path("change")
    public String changeProtocol() {
        return uriInfo.getAbsolutePath().toString();
    }
}
