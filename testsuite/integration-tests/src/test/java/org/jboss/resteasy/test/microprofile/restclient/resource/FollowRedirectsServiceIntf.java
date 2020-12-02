package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RegisterRestClient(baseUri ="http://localhost:8080/followRedirects_service",
configKey = "ckName")
@Path("/theService")
@Singleton
public interface FollowRedirectsServiceIntf {
    @GET
    @Path("get")
    List<String> getList();

    @GET
    @Path("tmpRedirect/{p}/{testname}")
    Response tmpRedirect(@PathParam("p") String p,
                         @PathParam("testname") String testname);

    @Path("post-redirect")
    @POST
    Response postRedirect(String testname);

    @GET
    @Path("movedPermanently/{p}/{testname}")
    Response movedPermanently(@PathParam("p") String p,
                              @PathParam("testname") String testname);

    @GET
    @Path("found/{p}/{testname}")
    Response found(@PathParam("p") String p,
                   @PathParam("testname") String testname);

    @GET
    @Path("ping")
    String ping();
}
