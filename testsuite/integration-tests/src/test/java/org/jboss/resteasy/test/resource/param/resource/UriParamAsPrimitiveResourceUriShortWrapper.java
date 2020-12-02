package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/short/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriShortWrapper {
   @GET
   public String doGet(@PathParam("arg") Short v) {
      Assert.assertTrue(UriParamAsPrimitiveTest.ERROR_CODE, 32767 == v.shortValue());
      return "content";
   }
}
