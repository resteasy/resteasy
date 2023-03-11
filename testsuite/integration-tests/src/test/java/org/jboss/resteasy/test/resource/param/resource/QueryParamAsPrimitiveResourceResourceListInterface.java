package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/list")
public interface QueryParamAsPrimitiveResourceResourceListInterface {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@QueryParam("boolean") List<Boolean> v);

    @GET
    @Produces("application/byte")
    String doGetByte(@QueryParam("byte") List<Byte> v);

    @GET
    @Produces("application/short")
    String doGetShort(@QueryParam("short") List<Short> v);

    @GET
    @Produces("application/int")
    String doGetInteger(@QueryParam("int") List<Integer> v);

    @GET
    @Produces("application/long")
    String doGetLong(@QueryParam("long") List<Long> v);

    @GET
    @Produces("application/float")
    String doGetFloat(@QueryParam("float") List<Float> v);

    @GET
    @Produces("application/double")
    String doGetDouble(@QueryParam("double") List<Double> v);

    @GET
    @Produces("application/char")
    String doGetCharacter(@QueryParam("char") List<Character> v);
}
