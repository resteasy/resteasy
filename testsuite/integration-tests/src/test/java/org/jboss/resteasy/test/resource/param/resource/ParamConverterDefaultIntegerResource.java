package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class ParamConverterDefaultIntegerResource {
   @PUT
   public void putDefault(@QueryParam("pojo") @DefaultValue("100") int q,
                           @MatrixParam("pojo") @DefaultValue("100") int mp, @DefaultValue("100") @HeaderParam("pojo") int hp) {
      Assert.assertEquals(100100, q);
      Assert.assertEquals(100100, mp);
      Assert.assertEquals(100100, hp);
   }
}
