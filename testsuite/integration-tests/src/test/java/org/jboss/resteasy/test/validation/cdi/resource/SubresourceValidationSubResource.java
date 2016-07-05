package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class SubresourceValidationSubResource {
    @Max(3)
    private int y = 7;

    @Path("{id}")
    @GET
    public void get(@Max(7) @PathParam("id") Long id, @Valid @BeanParam SubresourceValidationQueryBeanParam queryParams) {
    }

    @Path("return/{s}")
    @GET
    @Size(max = 3)
    public String getString(@PathParam("s") String s) {
        return s;
    }
}