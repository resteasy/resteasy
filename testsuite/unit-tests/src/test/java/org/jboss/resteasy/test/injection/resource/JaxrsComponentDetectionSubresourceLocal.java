package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.GET;

public interface JaxrsComponentDetectionSubresourceLocal {
   @GET
   void foo();
}
