package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class ParamConverterDefaultResource {
   @PUT
   public void putDefault(@QueryParam("pojo") @DefaultValue("default") ParamConverterPOJO q,
                           @MatrixParam("pojo") @DefaultValue("default") ParamConverterPOJO mp, @DefaultValue("default") @HeaderParam("pojo") ParamConverterPOJO hp) {
      Assert.assertEquals(q.getName(), "default");
      Assert.assertEquals(mp.getName(), "default");
      Assert.assertEquals(hp.getName(), "default");
   }
}
