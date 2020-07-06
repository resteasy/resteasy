package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.jboss.resteasy.utils.PortProviderUtil;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/theService")
public class FollowRedirectsService {
    private static final String prefix = "/thePatron";

    @GET
    @Path("get")
    public List<String> getList() {
        List<String> l = new ArrayList<>();
        l.add("theService reached");
        return l;
    }

    @GET
    @Path("tmpRedirect/{p}/{testname}")
    public Response tmpRedirect(@PathParam("p") String p,
                                @PathParam("testname") String testname){
        return Response.temporaryRedirect(
                PortProviderUtil.createURI("/"+p+"/redirected", testname))
                .build();
    }

    @Path("post-redirect")
    @POST
    public Response postRedirect(String testname) {
        return Response.seeOther(
                PortProviderUtil.createURI(prefix+"/redirected", testname))
                .build();
    }

    @GET
    @Path("movedPermanently/{p}/{testname}")
    public Response movedPermanently(@PathParam("p") String p,
                                     @PathParam("testname") String testname) {
        return Response.status(301).header("location",
                PortProviderUtil.createURI("/"+p+"/redirectedDirectResponse", testname))
                .build();
    }


    @GET
    @Path("found/{p}/{testname}")
    public Response found(@PathParam("p") String p,
                          @PathParam("testname") String testname) {
        return Response.status(302).header("location",
                PortProviderUtil.createURI("/"+p+"/redirectedDirectResponse", testname))
                .build();
    }

    @GET
    @Path("ping")
    public String ping() {
        return "pong";
    }
}
