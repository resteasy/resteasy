package org.jboss.resteasy.client.jaxrs;

import java.util.concurrent.Executor;

public interface ClientHttpEngineBuilder {
    ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder);

    /**
     * Sets the executor to use for the client engine.
     *
     * @param executor the executor to be used
     *
     * @return this engine builder
     */
    default ClientHttpEngineBuilder executor(final Executor executor) {
        return this;
    }

    ClientHttpEngine build();
}
