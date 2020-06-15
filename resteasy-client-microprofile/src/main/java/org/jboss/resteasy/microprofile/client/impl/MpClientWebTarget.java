package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

public class MpClientWebTarget extends ClientWebTarget {

   private List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories;

   protected MpClientWebTarget(final ResteasyClient client, final ClientConfiguration configuration,
                                final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories)
    {
        super(client, configuration);
        this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    public MpClientWebTarget(final ResteasyClient client, final String uri, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) throws IllegalArgumentException, NullPointerException
    {
       super(client, uri, configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    public MpClientWebTarget(final ResteasyClient client, final URI uri, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) throws NullPointerException
    {
       super(client, uri, configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    public MpClientWebTarget(final ResteasyClient client, final UriBuilder uriBuilder, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) throws NullPointerException
    {
       super(client, uriBuilder, configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    @Override
    protected ClientWebTarget newInstance(ResteasyClient client, UriBuilder uriBuilder, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uriBuilder, configuration, asyncInterceptorFactories);
    }

    @Override
    protected ClientInvocationBuilder createClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration) {
        return new MpClientInvocationBuilder(client, uri, configuration, asyncInterceptorFactories);
    }
}
