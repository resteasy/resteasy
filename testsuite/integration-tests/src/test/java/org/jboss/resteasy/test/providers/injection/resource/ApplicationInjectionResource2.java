package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

@Path("/")
public class ApplicationInjectionResource2 {

   @Context
   Application application;
   
   @Context
   Providers providers;
   
   @POST
   @Produces("text/plain")
   @Path("test/{param}")
   public Response test(String s, @PathParam("param") String p) {
      ContextResolver<String> resolver = providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE);
      return Response.ok(s + "|" + p + "|" + resolver.getContext(null) + "|" + getClass() + ":" + application.getClass().getName()).build();

   }
}
