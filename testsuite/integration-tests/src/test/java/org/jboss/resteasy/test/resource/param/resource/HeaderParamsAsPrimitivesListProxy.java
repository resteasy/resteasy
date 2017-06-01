package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/list")
public interface HeaderParamsAsPrimitivesListProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("boolean") List<Boolean> v);

    @GET
    @Produces("application/byte")
    String doGetByte(@HeaderParam("byte") List<Byte> v);

    @GET
    @Produces("application/short")
    String doGetShort(@HeaderParam("short") List<Short> v);

    @GET
    @Produces("application/int")
    String doGetInteger(@HeaderParam("int") List<Integer> v);

    @GET
    @Produces("application/long")
    String doGetLong(@HeaderParam("long") List<Long> v);

    @GET
    @Produces("application/float")
    String doGetFloat(@HeaderParam("float") List<Float> v);

    @GET
    @Produces("application/double")
    String doGetDouble(@HeaderParam("double") List<Double> v);
    
    @GET
    @Produces("application/char")
    String doGetCharacter(@HeaderParam("char") List<Character> v);
}
