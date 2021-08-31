package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array")
public class HeaderParamsAsPrimitivesResourceArray implements HeaderParamsAsPrimitivesArrayProxy {
   @GET
   @Produces("application/boolean")
   public String doGetBoolean(@HeaderParam("boolean") boolean[] v) {
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v[0]);
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v[1]);
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v[2]);
      return "content";
   }

   @GET
   @Produces("application/short")
   public String doGetShort(@HeaderParam("short") short[] v) {
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v[0]);
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v[0]);
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v[0]);
      return "content";
   }
}
