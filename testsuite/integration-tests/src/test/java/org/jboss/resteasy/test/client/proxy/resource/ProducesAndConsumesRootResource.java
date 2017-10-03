package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by rsearls on 9/18/17.
 */
@Produces("text/plain")
@Consumes("text/plain")
@Path("/greetings")
public interface ProducesAndConsumesRootResource {
   @GET
   public String getRootGreeting();
}
