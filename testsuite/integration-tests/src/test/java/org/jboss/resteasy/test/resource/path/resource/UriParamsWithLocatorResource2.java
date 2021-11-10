package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;

public class UriParamsWithLocatorResource2 {
   @GET
   @Path("/{id}")
   public String get(@PathParam("id") PathSegment id) {
      Assert.assertEquals("2", id.getPath());
      return id.getPath();
   }
}
