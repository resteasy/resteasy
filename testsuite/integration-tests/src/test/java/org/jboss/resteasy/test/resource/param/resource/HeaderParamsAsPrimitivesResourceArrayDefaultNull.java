package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array/default/null")
public class HeaderParamsAsPrimitivesResourceArrayDefaultNull {
   @GET
   @Produces("application/boolean")
   public String doGetBoolean(@HeaderParam("boolean") boolean[] v) {
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.length);
      return "content";
   }

   @GET
   @Produces("application/short")
   public String doGetShort(@HeaderParam("short") short[] v) {
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.length);
      return "content";
   }
}
