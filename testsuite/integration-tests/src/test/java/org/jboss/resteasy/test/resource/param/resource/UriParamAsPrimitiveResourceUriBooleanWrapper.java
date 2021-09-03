package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/boolean/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriBooleanWrapper {
   @GET
   public String doGet(@PathParam("arg") Boolean v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, true, v.booleanValue());
      return "content";
   }
}
