package org.jboss.resteasy.microprofile.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;


public class MpClientBuilderImpl extends ResteasyClientBuilderImpl {

    public final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories = new ArrayList<>();

    @Override
    protected ResteasyClient createResteasyClient(ClientHttpEngine engine, ExecutorService executor, boolean cleanupExecutor,
                                                  ScheduledExecutorService scheduledExecutorService, ClientConfiguration config) {
        return new MpClient(engine, executor, cleanupExecutor, scheduledExecutorService, config, asyncInterceptorFactories);
    }
}
