package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/")
public interface QueryParamAsPrimitiveResourceQueryPrimitivesInterface {
   @GET
   @Produces("application/boolean")
   String doGet(@QueryParam("boolean") boolean v);

   @GET
   @Produces("application/byte")
   String doGet(@QueryParam("byte") byte v);

   @GET
   @Produces("application/short")
   String doGet(@QueryParam("short") short v);

   @GET
   @Produces("application/int")
   String doGet(@QueryParam("int") int v);

   @GET
   @Produces("application/long")
   String doGet(@QueryParam("long") long v);

   @GET
   @Produces("application/float")
   String doGet(@QueryParam("float") float v);

   @GET
   @Produces("application/double")
   String doGet(@QueryParam("double") double v);

   @GET
   @Produces("application/char")
   String doGet(@QueryParam("char") char v);
}
