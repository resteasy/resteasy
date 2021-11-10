package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/tricky")
public class ComplexPathParamTrickyResource {
   @GET
   @Path("{hello}")
   public String getHello(@PathParam("hello") int one) {
      Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, one, 1);
      return "hello";
   }

   @GET
   @Path("{1},{2}")
   public String get2Groups(@PathParam("1") int one, @PathParam("2") int two) {
      Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, 1, one);
      Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, 2, two);
      return "2Groups";
   }

   @GET
   @Path("h{1}")
   public String getPrefixed(@PathParam("1") int one) {
      Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, 1, one);
      return "prefixed";
   }
}
