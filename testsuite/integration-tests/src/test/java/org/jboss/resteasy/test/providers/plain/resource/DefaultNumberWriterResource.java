package org.jboss.resteasy.test.providers.plain.resource;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("")
public class DefaultNumberWriterResource {
    @Path("test/{type}")
    @GET
    public Response get(@PathParam("type") String type) throws Exception {
        if ("Byte".equals(type)) {
            return Response.ok().entity(((byte) 123)).build();
        } else if ("byte".equals(type)) {
            return Response.ok().entity((byte) 123).build();
        } else if ("Double".equals(type)) {
            return Response.ok().entity((123.4)).build();
        } else if ("double".equals(type)) {
            return Response.ok().entity(123.4).build();
        } else if ("Float".equals(type)) {
            return Response.ok().entity(((float) 123.4)).build();
        } else if ("float".equals(type)) {
            return Response.ok().entity((float) 123.4).build();
        } else if ("Integer".equals(type)) {
            return Response.ok().entity((123)).build();
        } else if ("integer".equals(type)) {
            return Response.ok().entity(123).build();
        } else if ("Long".equals(type)) {
            return Response.ok().entity(((long) 123)).build();
        } else if ("long".equals(type)) {
            return Response.ok().entity((long) 123).build();
        } else if ("Short".equals(type)) {
            return Response.ok().entity(((short) 123)).build();
        } else if ("short".equals(type)) {
            return Response.ok().entity((short) 123).build();
        } else if ("bigDecimal".equals(type)) {
            return Response.ok().entity(new BigDecimal(123)).build();
        } else {
            throw new RuntimeException("unexpected type: " + type);
        }
    }
}
