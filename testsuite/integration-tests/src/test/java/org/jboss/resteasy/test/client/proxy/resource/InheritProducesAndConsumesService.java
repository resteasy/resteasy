package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by rsearls on 9/20/17.
 */
@Path("/greetings")
public class InheritProducesAndConsumesService {
   static String msg = "Service Hello";
   @GET
   @Produces("text/plain")
   public String get()
   {
      return msg;
   }
}
