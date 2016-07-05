package org.jboss.resteasy.spring.scanned;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/scanned")
public class ScannedResource
{
   @GET
   public String callGet()
   {
      return "Hello";
   }
}
