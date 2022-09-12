package org.jboss.resteasy.test.security.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/secured")
public interface BasicAuthBaseProxy {
   @GET
   String get();

   @GET
   @Path("/authorized")
   String getAuthorized();

   @GET
   @Path("/deny")
   String deny();

   @GET
   @Path("/failure")
   List<String> getFailure();
}
