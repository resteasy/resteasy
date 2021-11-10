package org.jboss.resteasy.test.resource.basic.resource;

import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;

@Path("/queryEscapedMatrParam")
public class UriInfoEscapedMatrParamResource {
   private static final String ERROR_MSG = "Wrong parameter";

   @GET
   public String doGet(@MatrixParam("a") String a, @MatrixParam("b") String b, @MatrixParam("c") String c, @MatrixParam("d") String d) {
      Assert.assertEquals(ERROR_MSG, "a;b", a);
      Assert.assertEquals(ERROR_MSG, "x/y", b);
      Assert.assertEquals(ERROR_MSG, "m\\n", c);
      Assert.assertEquals(ERROR_MSG, "k=l", d);
      return "content";
   }
}
