package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;

public interface InheritenceParentResource {

   @GET
   @Produces("text/plain")
   String firstest();

}
