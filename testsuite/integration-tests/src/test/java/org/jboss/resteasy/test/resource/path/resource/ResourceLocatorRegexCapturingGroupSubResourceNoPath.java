package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 * User: rsearls
 * Date: 2/17/17
 */
@Produces("text/plain")
public class ResourceLocatorRegexCapturingGroupSubResourceNoPath {
   private String name;

   public ResourceLocatorRegexCapturingGroupSubResourceNoPath(final String name) {
      this.name = name;
   }

   @GET
   public Response get() {
      return Response.ok(name +" success").build();
   }
}
