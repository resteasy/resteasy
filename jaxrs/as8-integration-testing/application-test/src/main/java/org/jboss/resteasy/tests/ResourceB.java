package org.jboss.resteasy.tests;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/resources")
public class ResourceB
{
   @Path("b")
   @GET
   public String get() {
      return "b";
   }
}
