package org.jboss.resteasy.test.resource.basic.resource;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

@ApplicationScoped
public class ParameterSubResClassSub {
    AtomicInteger resourceCounter = new AtomicInteger();
    @Inject
    ApplicationScopeObject appScope;

    @Inject
    RequestScopedObject requestScope;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public String get(@Context HttpHeaders headers) {
        Assertions.assertEquals("/path/subclass", uriInfo.getPath(),
                "Wrong path value from injected UriInfo");
        Assertions.assertNotNull(headers.getHeaderString("Connection"),
                "Connection header from injected HttpHeaders is null");
        return "resourceCounter:" + resourceCounter.incrementAndGet() + ",appscope:" + appScope.getCount() + ",requestScope:"
                + requestScope.getCount();
    }
}
