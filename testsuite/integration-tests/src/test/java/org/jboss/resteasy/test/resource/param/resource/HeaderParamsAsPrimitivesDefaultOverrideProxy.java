package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/default/override")
public interface HeaderParamsAsPrimitivesDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    String doGet(@HeaderParam("boolean") @DefaultValue("false") boolean v);

    @GET
    @Produces("application/byte")
    String doGet(@HeaderParam("byte") @DefaultValue("1") byte v);

    @GET
    @Produces("application/short")
    String doGet(@HeaderParam("short") @DefaultValue("1") short v);

    @GET
    @Produces("application/int")
    String doGet(@HeaderParam("int") @DefaultValue("1") int v);

    @GET
    @Produces("application/long")
    String doGet(@HeaderParam("long") @DefaultValue("1") long v);

    @GET
    @Produces("application/float")
    String doGet(@HeaderParam("float") @DefaultValue("0.0") float v);

    @GET
    @Produces("application/double")
    String doGet(@HeaderParam("double") @DefaultValue("0.0") double v);
    
    @GET
    @Produces("application/char")
    String doGet(@HeaderParam("char") @DefaultValue("b") char v);
}
