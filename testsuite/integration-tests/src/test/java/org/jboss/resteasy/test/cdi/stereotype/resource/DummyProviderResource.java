package org.jboss.resteasy.test.cdi.stereotype.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/provider")
public class DummyProviderResource
{
   @GET
   @Path("get")
   public Dummy getDummy() {
      return new Dummy("FooBar", 42);
   }
}
