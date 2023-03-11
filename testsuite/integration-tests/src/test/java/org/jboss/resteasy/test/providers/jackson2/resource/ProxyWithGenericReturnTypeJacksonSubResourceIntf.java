package org.jboss.resteasy.test.providers.jackson2.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ProxyWithGenericReturnTypeJacksonSubResourceIntf {
    @GET
    @Path("list")
    @Produces("application/*+json")
    List<ProxyWithGenericReturnTypeJacksonAbstractParent> resourceMethod();

    @GET
    @Path("one")
    @Produces("application/*+json")
    ProxyWithGenericReturnTypeJacksonAbstractParent resourceMethodOne();
}
