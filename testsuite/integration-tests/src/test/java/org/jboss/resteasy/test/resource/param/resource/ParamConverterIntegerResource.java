package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

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