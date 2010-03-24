package org.jboss.resteasy.cdi.test.alternative;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/alternative")
@Produces("text/plain")
public class ProductionResource
{
   @GET
   public String getValue()
   {
      return "ProductionResource";
   }
}
