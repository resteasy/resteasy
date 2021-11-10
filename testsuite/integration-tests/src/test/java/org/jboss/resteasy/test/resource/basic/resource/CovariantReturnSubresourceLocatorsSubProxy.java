package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;

public interface CovariantReturnSubresourceLocatorsSubProxy {
   @GET
   @Produces("text/plain")
   String get();
}
