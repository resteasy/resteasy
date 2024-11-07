package org.jboss.resteasy.client.jaxrs;

/**
 * @deprecated use the {@code org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory} instead
 */
@Deprecated(forRemoval = true, since = "7.0")
public interface ClientHttpEngineBuilder {
    ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder);

    ClientHttpEngine build();
}
