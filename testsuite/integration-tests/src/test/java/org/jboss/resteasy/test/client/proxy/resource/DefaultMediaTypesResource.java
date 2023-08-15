package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.jboss.resteasy.test.client.proxy.DefaultMediaTypesTest;

@Path("foo")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class DefaultMediaTypesResource implements DefaultMediaTypesTest.Foo {
    @Inject
    HttpRequest request;

    @Override
    public String getFoo() {
        return request.getHttpHeaders().getAcceptableMediaTypes().toString();
    }

    @Override
    public String setFoo(String value) {
        return request.getHttpHeaders().getMediaType().toString();
    }
}
