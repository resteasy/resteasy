package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class ParamConverterResource {
   @Path("{pojo}")
   @PUT
   public void put(@QueryParam("pojo") ParamConverterPOJO q, @PathParam("pojo") ParamConverterPOJO pp, @MatrixParam("pojo") ParamConverterPOJO mp,
               @HeaderParam("pojo") ParamConverterPOJO hp) {
      Assert.assertEquals(q.getName(), "pojo");
      Assert.assertEquals(pp.getName(), "pojo");
      Assert.assertEquals(mp.getName(), "pojo");
      Assert.assertEquals(hp.getName(), "pojo");
   }
}
