package org.jboss.resteasy.test.warning.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * Created by rsearls on 9/5/17.
 */
public class TestSubResource {

   @POST
   @Path("")
   public String sub(String s) {
      return "sub(" + s + ")";
   }
}
