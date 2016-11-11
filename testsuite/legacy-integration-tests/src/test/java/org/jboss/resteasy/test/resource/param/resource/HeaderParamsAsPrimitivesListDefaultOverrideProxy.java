package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/list/default/override")
public interface HeaderParamsAsPrimitivesListDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") List<Boolean> v);

    @GET
    @Produces("application/byte")
    String doGetByte(@HeaderParam("byte") @DefaultValue("0") List<Byte> v);

    @GET
    @Produces("application/short")
    String doGetShort(@HeaderParam("short") @DefaultValue("0") List<Short> v);

    @GET
    @Produces("application/int")
    String doGetInteger(@HeaderParam("int") @DefaultValue("0") List<Integer> v);

    @GET
    @Produces("application/long")
    String doGetLong(@HeaderParam("long") @DefaultValue("0") List<Long> v);

    @GET
    @Produces("application/float")
    String doGetFloat(@HeaderParam("float") @DefaultValue("0.0") List<Float> v);

    @GET
    @Produces("application/double")
    String doGetDouble(@HeaderParam("double") @DefaultValue("0.0") List<Double> v);
}
