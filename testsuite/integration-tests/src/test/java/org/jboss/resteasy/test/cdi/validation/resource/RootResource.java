package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("root")
public interface RootResource
{
   @Path("/sub")
   SubResource getSubResource();

   @Path("entered")
   @GET
   Response entered();
}
