package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/list/default")
public interface HeaderParamsAsPrimitivesListDefaultProxy {
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
    String doGetInteger();

    @GET
    @Produces("application/long")
    String doGetLong();

    @GET
    @Produces("application/float")
    String doGetFloat();

    @GET
    @Produces("application/double")
    String doGetDouble();

    @GET
    @Produces("application/char")
    String doGetCharacter();
}
