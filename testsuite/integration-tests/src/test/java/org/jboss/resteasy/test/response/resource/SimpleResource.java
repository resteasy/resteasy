package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * @author Ivo Studensky
 */
@Path("/simpleresource")
public class SimpleResource {

   @GET
   @Produces("text/plain")
   public String get() {
      return "hello";
   }
}
