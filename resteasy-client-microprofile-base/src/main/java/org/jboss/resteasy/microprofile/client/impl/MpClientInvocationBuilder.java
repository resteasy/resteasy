package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.microprofile.client.async.AsyncInterceptorRxInvoker;

import jakarta.ws.rs.client.CompletionStageRxInvoker;


public class MpClientInvocationBuilder extends ClientInvocationBuilder {

   private List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories;

   public MpClientInvocationBuilder(final ResteasyClient client, final URI uri, final ClientConfiguration configuration,
                                     final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) {
        super(client, uri, configuration);
        this.asyncInterceptorFactories = asyncInterceptorFactories;
    }

    @Override
    public CompletionStageRxInvoker rx() {
        return new AsyncInterceptorRxInvoker(this, invocation.getClient().asyncInvocationExecutor());
    }

    @Override
    protected ClientInvocation createClientInvocation(ClientInvocation invocation) {
        return new MpClientInvocation(invocation, asyncInterceptorFactories);
    }

    @Override
    protected ClientInvocation createClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers,
                                                      ClientConfiguration parent) {
        return new MpClientInvocation(client, uri, headers, parent, asyncInterceptorFactories);
    }
}
