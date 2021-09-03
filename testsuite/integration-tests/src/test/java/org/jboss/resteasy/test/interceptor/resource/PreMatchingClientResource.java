package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Created by rsearls on 8/21/17.
 */
@Path("/")
public class PreMatchingClientResource {
   @GET
   @Path("testIt")
   public String get() {
      return "OK";
   }

}
