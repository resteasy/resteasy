/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.embedded.test.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InterceptorBinding;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that CDI interceptors are invoked on resource methods.
 */
@RestBootstrap(value = CdiInterceptorTest.InterceptedResource.class)
@Tag("cdi")
class CdiInterceptorTest {

    @Test
    void interceptorInvoked(@RestResource @RequestPath("interceptor/greet") final WebTarget target) {
        final String result = target.request().get(String.class);
        Assertions.assertEquals("[intercepted] Hello", result);
    }

    @Test
    void nonInterceptedMethod(@RestResource @RequestPath("interceptor/plain") final WebTarget target) {
        final String result = target.request().get(String.class);
        Assertions.assertEquals("Plain", result);
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @InterceptorBinding
    @Inherited
    public @interface Logged {
    }

    @Interceptor
    @Logged
    @Priority(Interceptor.Priority.APPLICATION)
    public static class LoggingInterceptor {

        @AroundInvoke
        public Object intercept(final InvocationContext ctx) throws Exception {
            final Object result = ctx.proceed();
            if (result instanceof String) {
                return "[intercepted] " + result;
            }
            return result;
        }
    }

    @RequestScoped
    public static class GreetingService {
        public String greet() {
            return "Hello";
        }
    }

    @Path("/interceptor")
    public static class InterceptedResource {

        @Inject
        GreetingService greetingService;

        @GET
        @Path("/greet")
        @Produces(MediaType.TEXT_PLAIN)
        @Logged
        public String greet() {
            return greetingService.greet();
        }

        @GET
        @Path("/plain")
        @Produces(MediaType.TEXT_PLAIN)
        public String plain() {
            return "Plain";
        }
    }
}
