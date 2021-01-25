package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public class ParamConverterIntegerResource {
   @Path("{pojo}")
   @PUT
   public void put(@QueryParam("pojo") int q, @PathParam("pojo") int pp, @MatrixParam("pojo") int mp,
               @HeaderParam("pojo") int hp) {
      Assert.assertEquals(44, q);
      Assert.assertEquals(44, pp);
      Assert.assertEquals(44, mp);
      Assert.assertEquals(44, hp);
   }
}