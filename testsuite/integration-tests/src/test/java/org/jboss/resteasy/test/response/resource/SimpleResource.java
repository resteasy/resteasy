package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
