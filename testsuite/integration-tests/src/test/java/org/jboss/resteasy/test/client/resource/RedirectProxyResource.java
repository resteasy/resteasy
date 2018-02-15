package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public interface RedirectProxyResource
{

    @Path("redirect/{p}")
    @GET
    Response redirect(@PathParam("p") String p);

    @Path("redirected")
    @GET
    Response redirected();


    @Path("redirectDirectResponse/{p}")
    @GET
    Response redirectDirectResponse(@PathParam("p") String p);

    @Path("redirectedDirectResponse")
    @GET
    String redirectedDirectResponse();

    @Path("movedPermanently/{p}")
    @GET
    Response movedPermanently(@PathParam("p") String p);


    @Path("found/{p}")
    @GET
    Response found(@PathParam("p") String p);
}
