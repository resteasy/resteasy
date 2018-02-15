package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.utils.PortProviderUtil;

@Path("/")
public class RedirectResource
{
    @Path("redirect/{p}")
    @GET
    public Response redirect(@PathParam("p") String p)
    {
        return Response.temporaryRedirect(PortProviderUtil.createURI("/redirected", p)).build();
    }

    @Path("redirected")
    @GET
    public Response redirected()
    {
        return Response.ok("OK").build();
    }

    @Path("post-redirect")
    @POST
    public Response postRedirect(String p)
    {
        return Response.seeOther(PortProviderUtil.createURI("/redirected", p)).build();
    }

    @Path("redirectDirectResponse/{p}")
    @GET
    public Response redirectDirectResponse(@PathParam("p") String p)
    {
        return Response.temporaryRedirect(PortProviderUtil.createURI("/redirectedDirectResponse", p)).build();
    }

    @Path("redirectedDirectResponse")
    @GET
    public String redirectedDirectResponse()
    {
        return "ok - direct response";
    }


    @Path("movedPermanently/{p}")
    @GET
    public Response movedPermanently(@PathParam("p") String p)
    {
        return Response.status(301).header("location", PortProviderUtil.createURI("/redirectedDirectResponse", p)).build();
    }

    @Path("found/{p}")
    @GET
    public Response found(@PathParam("p") String p)
    {
        return Response.status(302).header("location", PortProviderUtil.createURI("/redirectedDirectResponse", p)).build();
    }
}
