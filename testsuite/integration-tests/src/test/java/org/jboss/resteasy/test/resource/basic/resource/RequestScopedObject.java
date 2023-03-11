package org.jboss.resteasy.test.resource.basic.resource;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class RequestScopedObject {

    AtomicInteger count = new AtomicInteger();

    public int getCount() {
        return count.incrementAndGet();
    }
}
