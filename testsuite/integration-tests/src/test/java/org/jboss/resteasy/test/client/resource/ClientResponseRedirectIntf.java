package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/redirect")
public interface ClientResponseRedirectIntf {
   @GET
   Response get();
}
