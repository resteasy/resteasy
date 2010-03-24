package org.jboss.resteasy.cdi.test.alternative;

import javax.enterprise.inject.Alternative;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/alternative")
@Alternative
public class MockResource extends ProductionResource
{
   @Override
   @GET
   public String getValue()
   {
      return "MockResource";
   }
}
