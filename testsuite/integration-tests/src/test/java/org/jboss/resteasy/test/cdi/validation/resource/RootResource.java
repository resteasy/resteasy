package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("root")
public interface RootResource
{
   @Path("/sub")
   SubResource getSubResource();

   @Path("entered")
   @GET
   Response entered();
}
