package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/default/null")
public interface HeaderParamsAsPrimitivesDefaultNullProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean();

   @GET
   @Produces("application/byte")
   String doGetByte();

   @GET
   @Produces("application/short")
   String doGetShort();

   @GET
   @Produces("application/int")
   String doGetInt();

   @GET
   @Produces("application/long")
   String doGetLong();

   @GET
   @Produces("application/float")
   String doGetFloat();

   @GET
   @Produces("application/double")
   String doGet();

   @GET
   @Produces("application/char")
   String doGetChar();
}
