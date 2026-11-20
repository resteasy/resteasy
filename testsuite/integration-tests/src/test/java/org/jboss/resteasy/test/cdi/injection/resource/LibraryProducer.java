/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import java.lang.reflect.Proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class LibraryProducer {

    @Produces
    @RequestScoped
    public HttpServletResponse produceResponse() {
        return (HttpServletResponse) Proxy.newProxyInstance(
                LibraryProducer.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> {
                    if ("getHeader".equals(method.getName()) && args != null && "X-Library-Producer".equals(args[0])) {
                        return "library-response";
                    }
                    return null;
                });
    }

    @Produces
    @ApplicationScoped
    public Client produceClient() {
        return ClientBuilder.newBuilder()
                .property("test.library.producer", "library-client")
                .build();
    }
}
