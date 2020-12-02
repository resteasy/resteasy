package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientImpl;


public class MpClient extends ResteasyClientImpl {

   private List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories;
   private QueryParamStyle queryParamStyle = null;

   public MpClient(final ClientHttpEngine engine, final ExecutorService executor, final boolean cleanupExecutor,
                    final ScheduledExecutorService scheduledExecutorService, final ClientConfiguration config,
                    final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) {
        super(engine, executor, cleanupExecutor, scheduledExecutorService, config);
        this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, String uri, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uri, configuration, asyncInterceptorFactories,
                queryParamStyle);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, URI uri, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uri, configuration, asyncInterceptorFactories,
                queryParamStyle);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, UriBuilder uriBuilder, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uriBuilder, configuration, asyncInterceptorFactories,
                queryParamStyle);
    }

    public void setQueryParamStyle(QueryParamStyle queryParamStyle) {
        this.queryParamStyle = queryParamStyle;
    }
}
