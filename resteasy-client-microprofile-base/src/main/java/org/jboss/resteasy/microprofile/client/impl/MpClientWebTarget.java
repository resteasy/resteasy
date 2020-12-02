package org.jboss.resteasy.microprofile.client.impl;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

public class MpClientWebTarget extends ClientWebTarget {

   private List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories;
   private QueryParamStyle queryParamStyle;

   protected MpClientWebTarget(final ResteasyClient client, final ClientConfiguration configuration,
                                final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories,
                                final QueryParamStyle queryParamStyle)
    {
        super(client, configuration);
        this.asyncInterceptorFactories = asyncInterceptorFactories;
        this.queryParamStyle = queryParamStyle;
    }

    public MpClientWebTarget(final ResteasyClient client, final String uri, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories,
                             final QueryParamStyle queryParamStyle) throws IllegalArgumentException, NullPointerException
    {
       super(client, new MpUriBuilder().uri(uri,queryParamStyle), configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
       this.queryParamStyle = queryParamStyle;
    }

    public MpClientWebTarget(final ResteasyClient client, final URI uri, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories,
                             final QueryParamStyle queryParamStyle) throws NullPointerException
    {
       super(client, new MpUriBuilder().uri(uri,queryParamStyle), configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
       this.queryParamStyle = queryParamStyle;
    }

    public MpClientWebTarget(final ResteasyClient client, final UriBuilder uriBuilder, final ClientConfiguration configuration,
                             final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories,
                             final QueryParamStyle queryParamStyle) throws NullPointerException
    {
       super(client, uriBuilder, configuration);
       this.asyncInterceptorFactories = asyncInterceptorFactories;
       this.queryParamStyle = queryParamStyle;
    }

    @Override
    protected ClientWebTarget newInstance(ResteasyClient client, UriBuilder uriBuilder, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uriBuilder, configuration, asyncInterceptorFactories, queryParamStyle);
    }

    @Override
    protected ClientInvocationBuilder createClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration) {
        return new MpClientInvocationBuilder(client, uri, configuration, asyncInterceptorFactories);
    }

}
