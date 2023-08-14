package org.jboss.resteasy.test.response.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("super")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class InheritedContextService {
    @Inject
    protected UriInfo uriInfo;

    @Inject
    protected HttpHeaders httpHeaders;

    @Inject
    protected Request request;

    @Inject
    protected SecurityContext securityContext;

    @Inject
    protected Providers providers;

    @Inject
    protected ResourceContext resourceContext;

    @Inject
    protected Configuration configuration;

    @Path("test/{level}")
    @GET
    public String test(@PathParam("level") String level) {
        return Boolean.toString(level.equals("BaseService") && testContexts());
    }

    protected boolean testContexts() {
        return uriInfo != null
                && httpHeaders != null
                && request != null
                && securityContext != null
                && providers != null
                && resourceContext != null
                && configuration != null;
    }
}
