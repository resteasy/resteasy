package org.jboss.resteasy.test.resource.resource;

import java.util.OptionalLong;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("optional_not_supported")
public class NotSupportedOpitionalPathParamResource {
    @Path("/path/{value}")
    @GET
    public String path(@PathParam("value") OptionalLong value) {
        return Long.toString(value.orElse(42));
    }

}
