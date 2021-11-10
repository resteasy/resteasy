package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/array")
public interface QueryParamAsPrimitiveResourceResourceArray {
   @GET
   @Produces("application/boolean")
   String doGetBoolean(@QueryParam("boolean") boolean[] v);

   @GET
   @Produces("application/byte")
   String doGetByte(@QueryParam("byte") byte[] v);

   @POST
   @Path("/non/existing/end/point")
   @Produces("application/byte")
   String doPostByte(@QueryParam("byte") byte[] v);

   @GET
   @Produces("application/short")
   String doGetShort(@QueryParam("short") short[] v);

   @GET
   @Produces("application/int")
   String doGetInteger(@QueryParam("int") int[] v);

   @GET
   @Produces("application/long")
   String doGetLong(@QueryParam("long") long[] v);

   @GET
   @Produces("application/float")
   String doGetFloat(@QueryParam("float") float[] v);

   @GET
   @Produces("application/double")
   String doGetDouble(@QueryParam("double") double[] v);

   @GET
   @Produces("application/char")
   String doGetCharacter(@QueryParam("char") char[] v);
}
