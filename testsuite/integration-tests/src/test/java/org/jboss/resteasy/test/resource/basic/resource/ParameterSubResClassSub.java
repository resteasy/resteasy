package org.jboss.resteasy.test.resource.basic.resource;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

@ApplicationScoped
public class ParameterSubResClassSub {
    AtomicInteger resourceCounter = new AtomicInteger();
    @Inject
    ApplicationScopeObject appScope;

    @Inject
    RequestScopedObject requestScope;

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public String get(@Context HttpHeaders headers) {
        Assert.assertEquals("Wrong path value from injected UriInfo", "/path/subclass", uriInfo.getPath());
        Assert.assertNotNull("Host header from injected HttpHeaders is null", headers.getHeaderString(HttpHeaders.HOST));
        return "resourceCounter:" + resourceCounter.incrementAndGet() + ",appscope:" + appScope.getCount() + ",requestScope:"
                + requestScope.getCount();
    }
}
