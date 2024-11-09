package org.jboss.resteasy.client.jaxrs;

/**
 * @deprecated use the {@code org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory} instead
 */
@Deprecated(forRemoval = true, since = "6.2")
public interface ClientHttpEngineBuilder {
    ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder);

    ClientHttpEngine build();
}
