package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class PathLimitedLocatorResource {
   @Path(value = "/locator{p:.*}")
   public Object get() {
      return new PathLimitedBasicResource();
   }

   @Path(value = "/locator2/{param:.*}")
   public Object get(@PathParam("param") String param, @QueryParam("expected") String expected) {
      Assert.assertEquals("Wrong parameter", param, expected);
      return new PathLimitedBasicResource();
   }
}
