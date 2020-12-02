package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;


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
