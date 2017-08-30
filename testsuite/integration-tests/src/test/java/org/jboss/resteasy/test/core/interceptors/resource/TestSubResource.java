package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

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

