package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/")
public interface RedirectProxyResource
{

   @Path("redirect/{p}")
   @GET
   Response redirect(@PathParam("p") String p);

   @Path("redirected")
   @GET
   Response redirected();


   @Path("redirectDirectResponse/{p}")
   @GET
   Response redirectDirectResponse(@PathParam("p") String p);

   @Path("redirectedDirectResponse")
   @GET
   String redirectedDirectResponse();

   @Path("movedPermanently/{p}")
   @GET
   Response movedPermanently(@PathParam("p") String p);


   @Path("found/{p}")
   @GET
   Response found(@PathParam("p") String p);
}
