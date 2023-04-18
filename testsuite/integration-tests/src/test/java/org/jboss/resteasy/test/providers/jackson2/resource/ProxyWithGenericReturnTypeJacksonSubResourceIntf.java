package org.jboss.resteasy.test.providers.jackson2.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
