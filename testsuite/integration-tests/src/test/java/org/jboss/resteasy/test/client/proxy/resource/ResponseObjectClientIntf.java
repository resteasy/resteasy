package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("test")
public interface ResponseObjectClientIntf {
   @GET
   ResponseObjectBasicObjectIntf get();

   @GET
   @Path("link-header")
   ResponseObjectHateoasObject performGetBasedOnHeader();
}
