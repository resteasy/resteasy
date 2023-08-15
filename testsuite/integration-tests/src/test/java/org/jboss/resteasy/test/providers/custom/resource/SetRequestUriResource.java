package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("base/resource")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class SetRequestUriResource {

    @Inject
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
