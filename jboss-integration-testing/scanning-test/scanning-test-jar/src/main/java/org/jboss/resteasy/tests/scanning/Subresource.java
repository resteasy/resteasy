package org.jboss.resteasy.tests.scanning;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Subresource
{
   @Path("doit")
   @GET
   @Produces("text/plain")
   public String get()
   {
      return "subresource-doit";
   }
}
