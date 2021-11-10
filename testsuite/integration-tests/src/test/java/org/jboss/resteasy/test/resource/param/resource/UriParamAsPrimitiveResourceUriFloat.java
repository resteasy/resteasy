package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/float/{arg}")
public class UriParamAsPrimitiveResourceUriFloat {
   @GET
   public String doGet(@PathParam("arg") float v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 3.14159265f, v, 0.0f);
      return "content";
   }
}
