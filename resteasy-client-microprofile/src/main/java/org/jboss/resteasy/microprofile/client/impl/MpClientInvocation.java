package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestContextImpl;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.core.ResteasyContext;


public class MpClientInvocation extends ClientInvocation {

    public static final String CONTAINER_HEADERS = "MP_CLIENT_CONTAINER_HEADERS";

    private MultivaluedMap<String, String> containerHeaders;

    protected MpClientInvocation(final ClientInvocation clientInvocation) {
        super(clientInvocation);
        captureContainerHeaders();
    }

    protected MpClientInvocation(final ResteasyClient client, final URI uri, final ClientRequestHeaders headers, final ClientConfiguration parent) {
        super(client, uri, headers, parent);
        captureContainerHeaders();
    }

    private void captureContainerHeaders() {
        HttpHeaders containerHeaders = ResteasyContext.getContextData(HttpHeaders.class);
        if(containerHeaders != null) {
            this.containerHeaders = containerHeaders.getRequestHeaders();
        }
    }

    @Override
    protected ClientResponse filterRequest(ClientRequestContextImpl requestContext) {
        if(containerHeaders != null) {
            requestContext.setProperty(CONTAINER_HEADERS, containerHeaders);
        }
        return super.filterRequest(requestContext);
    }
}
