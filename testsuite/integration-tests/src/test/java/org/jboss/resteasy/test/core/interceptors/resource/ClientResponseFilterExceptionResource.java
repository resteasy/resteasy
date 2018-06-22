package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("dummyservice")
public interface ClientResponseFilterExceptionResource {

   @Path("dummy")
   @GET
   public String dummy();
}
