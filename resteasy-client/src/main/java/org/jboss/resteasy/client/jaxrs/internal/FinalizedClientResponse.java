package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

/**
 * A class that adds a {@link Object#finalize) method to the {@link ClientResponse} as a last ditch backstop to prevent
 * leaking resources with ill-behaved clients.  Use of finalize could incur a significant performance penalty.
 */
public abstract class FinalizedClientResponse extends ClientResponse {

    protected FinalizedClientResponse(final ClientConfiguration configuration,
                                      final RESTEasyTracingLogger tracingLogger)
    {
        super(configuration, tracingLogger);
    }

    @Override
    // This method is synchronized to protect against premature calling of finalize by the GC
    protected synchronized void finalize() throws Throwable
    {
        if (isClosed()) return;
        try {
            close();
        }
        catch (Exception ignored) {
        }
    }

}
