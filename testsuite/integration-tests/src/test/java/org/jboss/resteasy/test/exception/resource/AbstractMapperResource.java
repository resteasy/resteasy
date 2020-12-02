package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("resource")
public class AbstractMapperResource {
   @GET
   @Path("custom")
   public String custom() throws Throwable {
      throw new AbstractMapperException("hello");
   }
}
