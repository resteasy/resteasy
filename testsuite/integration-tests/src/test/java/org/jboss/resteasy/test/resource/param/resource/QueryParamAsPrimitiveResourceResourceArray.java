package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/array")
public interface QueryParamAsPrimitiveResourceResourceArray {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@QueryParam("boolean") boolean[] v);

    @GET
    @Produces("application/byte")
    String doGetByte(@QueryParam("byte") byte[] v);

    @GET
    @Produces("application/short")
    String doGetShort(@QueryParam("short") short[] v);

    @GET
    @Produces("application/int")
    String doGetInteger(@QueryParam("int") int[] v);

    @GET
    @Produces("application/long")
    String doGetLong(@QueryParam("long") long[] v);

    @GET
    @Produces("application/float")
    String doGetFloat(@QueryParam("float") float[] v);

    @GET
    @Produces("application/double")
    String doGetDouble(@QueryParam("double") double[] v);
    
    @GET
    @Produces("application/char")
    String doGetCharacter(@QueryParam("char") char[] v); 
}
