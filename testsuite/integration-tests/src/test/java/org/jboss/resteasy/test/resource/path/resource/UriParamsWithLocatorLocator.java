package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/")
public class UriParamsWithLocatorLocator {
   @Path("/{id}")
   public UriParamsWithLocatorResource get(@PathParam("id") String id) {
      Assert.assertEquals("1", id);
      return new UriParamsWithLocatorResource();

   }
}
