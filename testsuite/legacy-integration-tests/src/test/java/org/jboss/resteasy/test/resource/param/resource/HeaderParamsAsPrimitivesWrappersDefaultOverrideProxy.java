package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/wrappers/default/override")
public interface HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    String doGet(@HeaderParam("boolean") @DefaultValue("false") Boolean v);

    @GET
    @Produces("application/byte")
    String doGet(@HeaderParam("byte") @DefaultValue("1") Byte v);

    @GET
    @Produces("application/short")
    String doGet(@HeaderParam("short") @DefaultValue("1") Short v);

    @GET
    @Produces("application/int")
    String doGet(@HeaderParam("int") @DefaultValue("1") Integer v);

    @GET
    @Produces("application/long")
    String doGet(@HeaderParam("long") @DefaultValue("1") Long v);

    @GET
    @Produces("application/float")
    String doGet(@HeaderParam("float") @DefaultValue("0.0") Float v);

    @GET
    @Produces("application/double")
    String doGet(@HeaderParam("double") @DefaultValue("0.0") Double v);
}
