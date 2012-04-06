package org.jboss.resteasy.tests.scanning;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/test")
public interface MyResource
{
   @Path("/subrsource")
   Subresource doit();

   @Path("/doit")
   @GET
   @Produces("text/plain")
   String get();
}
