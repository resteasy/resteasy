package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array/default/override")
public class HeaderParamsAsPrimitivesResourceArrayDefaultOverride
      implements
      HeaderParamsAsPrimitivesArrayDefaultOverrideProxy {
   @GET
   @Produces("application/boolean")
   public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v) {
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v[0]);
      return "content";
   }

   @GET
   @Produces("application/short")
   public String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v) {
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v[0]);
      return "content";
   }
}
