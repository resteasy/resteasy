package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/wrappers")
public interface HeaderParamsAsPrimitivesWrappersProxy {
   @GET
   @Produces("application/boolean")
   String doGet(@HeaderParam("boolean") Boolean v);

   @GET
   @Produces("application/byte")
   String doGet(@HeaderParam("byte") Byte v);

   @GET
   @Produces("application/short")
   String doGet(@HeaderParam("short") Short v);

   @GET
   @Produces("application/int")
   String doGet(@HeaderParam("int") Integer v);

   @GET
   @Produces("application/long")
   String doGet(@HeaderParam("long") Long v);

   @GET
   @Produces("application/float")
   String doGet(@HeaderParam("float") Float v);

   @GET
   @Produces("application/double")
   String doGet(@HeaderParam("double") Double v);

   @GET
   @Produces("application/char")
   String doGet(@HeaderParam("char") Character v);
}
