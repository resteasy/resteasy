package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/wrappers")
public interface QueryParamAsPrimitiveResourceResourceWrappersInterface {
   @GET
   @Produces("application/boolean")
   String doGet(@QueryParam("boolean") Boolean v);

   @GET
   @Produces("application/byte")
   String doGet(@QueryParam("byte") Byte v);

   @GET
   @Produces("application/short")
   String doGet(@QueryParam("short") Short v);

   @GET
   @Produces("application/int")
   String doGet(@QueryParam("int") Integer v);

   @GET
   @Produces("application/long")
   String doGet(@QueryParam("long") Long v);

   @GET
   @Produces("application/float")
   String doGet(@QueryParam("float") Float v);

   @GET
   @Produces("application/double")
   String doGet(@QueryParam("double") Double v);

   @GET
   @Produces("application/char")
   String doGet(@QueryParam("char") Character v);
}
