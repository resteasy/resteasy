package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public interface HeaderParamsAsPrimitivesPrimitivesProxy {
    @GET
    @Produces("application/boolean")
    String doGet(@HeaderParam("boolean") boolean v);

    @GET
    @Produces("application/byte")
    String doGet(@HeaderParam("byte") byte v);

    @GET
    @Produces("application/short")
    String doGet(@HeaderParam("short") short v);

    @GET
    @Produces("application/int")
    String doGet(@HeaderParam("int") int v);

    @GET
    @Produces("application/long")
    String doGet(@HeaderParam("long") long v);

    @GET
    @Produces("application/float")
    String doGet(@HeaderParam("float") float v);

    @GET
    @Produces("application/double")
    String doGet(@HeaderParam("double") double v);

    @GET
    @Produces("application/char")
    String doGet(@HeaderParam("char") char v);
}
