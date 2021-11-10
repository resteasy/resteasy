package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/long/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriLongWrapper {
   @GET
   public String doGet(@PathParam("arg") Long v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 9223372036854775807L, v.longValue());
      return "content";
   }
}
