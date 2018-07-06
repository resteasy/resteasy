package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

class JettyClientResponse extends ClientResponse {
    private final Runnable cancel;
    private InputStream stream;

    JettyClientResponse(ClientConfiguration configuration, InputStream stream, Runnable cancel) {
        super(configuration);
        this.cancel = cancel;
        this.stream = stream;
    }

    @Override
    protected InputStream getInputStream() {
        return stream;
    }

    @Override
    protected void setInputStream(InputStream is) {
        stream = is;
        resetEntity();
    }

    @Override
    public void releaseConnection() throws IOException {
        releaseConnection(false);
    }

	@Override
	public void releaseConnection(boolean consumeInputStream) throws IOException {
        InputStream is = getInputStream();
        if (is != null && consumeInputStream)
        {
           while (is.read() > 0)
           {
           }
        }
        cancel.run();
	}
	
}
