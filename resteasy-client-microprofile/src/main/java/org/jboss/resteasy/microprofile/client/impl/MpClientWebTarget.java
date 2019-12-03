package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

public class MpClientWebTarget extends ClientWebTarget {

    protected MpClientWebTarget(final ResteasyClient client, final ClientConfiguration configuration)
    {
        super(client, configuration);
    }

    public MpClientWebTarget(final ResteasyClient client, final String uri, final ClientConfiguration configuration) throws IllegalArgumentException, NullPointerException
    {
       super(client, uri, configuration);
    }

    public MpClientWebTarget(final ResteasyClient client, final URI uri, final ClientConfiguration configuration) throws NullPointerException
    {
       super(client, uri, configuration);
    }

    public MpClientWebTarget(final ResteasyClient client, final UriBuilder uriBuilder, final ClientConfiguration configuration) throws NullPointerException
    {
       super(client, uriBuilder, configuration);
    }

    @Override
    protected ClientWebTarget newInstance(ResteasyClient client, UriBuilder uriBuilder, ClientConfiguration configuration) {
        return new MpClientWebTarget(client, uriBuilder, configuration);
    }

    @Override
    protected ClientInvocationBuilder createClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration) {
        return new MpClientInvocationBuilder(client, uri, configuration);
    }
}
