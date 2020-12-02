package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class SpecialCharsInUrlResource {

   private static final String decodedPart = "foo+bar gee@foo.com";
   private static final String queryDecodedPart = "foo bar gee@foo.com";

   @Path("/simple/{bar}")
   @GET
   public String get(@PathParam("bar") String pathParam, @QueryParam("foo") String queryParam) {
      Assert.assertEquals(decodedPart, pathParam);
      Assert.assertEquals(queryDecodedPart, queryParam);
      return pathParam;
   }
}
