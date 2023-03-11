package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.InputStream;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

class JettyClientResponse extends ClientResponse {

    JettyClientResponse(final ClientConfiguration configuration, final InputStream stream) {
        super(configuration, RESTEasyTracingLogger.empty());
        setInputStream(stream);
    }

    @Override
    protected void setInputStream(final InputStream is) {
        this.is = is;
        resetEntity();
    }
}
