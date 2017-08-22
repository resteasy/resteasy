package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
