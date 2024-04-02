package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RequestScoped
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
