package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/char/{arg}")
public class UriParamAsPrimitiveResourceUriChar {
   @GET
   public String doGet(@PathParam("arg") char v) {
      Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 'a', v);
      return "content";
   }
}
