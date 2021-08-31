package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/int/{arg}")
public class UriParamAsPrimitiveResourceUriInt {
   @GET
   public String doGet(@PathParam("arg") int v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 2147483647, v);
      return "content";
   }
}
