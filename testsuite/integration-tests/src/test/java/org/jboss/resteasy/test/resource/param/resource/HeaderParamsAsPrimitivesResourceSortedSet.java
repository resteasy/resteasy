package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.SortedSet;

@Path("/sortedset")
public class HeaderParamsAsPrimitivesResourceSortedSet implements HeaderParamsAsPrimitivesSortedSetProxy {
   @GET
   @Produces("application/boolean")
   public String doGetBoolean(@HeaderParam("header") SortedSet<String> v) {
      Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2, v.size());
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, v.contains("one"));
      Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, v.contains("two"));
      return "content";
   }
}
