package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
}
