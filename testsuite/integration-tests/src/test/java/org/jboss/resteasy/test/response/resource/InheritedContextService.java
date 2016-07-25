package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;


@Path("super")
public class InheritedContextService {
    @Context
    protected UriInfo uriInfo;

    @Context
    protected HttpHeaders httpHeaders;

    @Context
    protected Request request;

    @Context
    protected SecurityContext securityContext;

    @Context
    protected Providers providers;

    @Context
    protected ResourceContext resourceContext;

    @Context
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
