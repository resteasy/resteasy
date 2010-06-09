package org.jboss.resteasy.test.interfaced;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author Magesh Kumar B
 * @version $Revision: 1 $
 */
@Path("/")
public interface EchoBase
{
   @GET
   @Path("/{message}")
   public String echo(@PathParam("message") String message);
}
