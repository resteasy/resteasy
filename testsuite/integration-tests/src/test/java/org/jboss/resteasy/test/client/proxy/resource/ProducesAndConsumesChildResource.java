package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by rsearls on 9/18/17.
 */
@Path("/greetings")
public interface ProducesAndConsumesChildResource extends ProducesAndConsumesRootResource
{
   @GET
   public String getChildGreeting();
}
