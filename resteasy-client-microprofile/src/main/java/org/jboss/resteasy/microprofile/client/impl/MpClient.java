package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientImpl;


public class MpClient extends ResteasyClientImpl {

    public MpClient(final ClientHttpEngine engine, final ExecutorService executor, final boolean cleanupExecutor,
                    final ScheduledExecutorService scheduledExecutorService, final ClientConfiguration config) {
        super(engine, executor, cleanupExecutor, scheduledExecutorService, config);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, String uri, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uri, configuration);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, URI uri, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uri, configuration);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, UriBuilder uriBuilder, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uriBuilder, configuration);
    }

}
