package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/float/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriFloatWrapper {
   @GET
   public String doGet(@PathParam("arg") Float v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 3.14159265f, v.floatValue(), 0.0f);
      return "content";
   }
}
