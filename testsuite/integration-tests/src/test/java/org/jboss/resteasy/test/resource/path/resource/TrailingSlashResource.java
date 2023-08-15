package org.jboss.resteasy.test.resource.path.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("test")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class TrailingSlashResource {
    @Inject
    private UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public Response test() {
        return Response.ok(uriInfo.getPath()).build();
    }
}
