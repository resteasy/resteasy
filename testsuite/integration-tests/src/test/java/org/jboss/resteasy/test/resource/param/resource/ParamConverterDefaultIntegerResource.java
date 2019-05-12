package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

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
