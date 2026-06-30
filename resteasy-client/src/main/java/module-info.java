/**
 * RESTEasy Client implementation module.
 * <p>
 * This module provides the complete Jakarta RESTful Web Services client implementation,
 * including HTTP client engines for Apache HttpClient and Eclipse Jetty.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.client {
    // JDK modules
    requires java.management;  // For statistics/monitoring

    // Third-party dependencies
    requires org.apache.commons.codec;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.jboss.logging;
    requires org.reactivestreams;
    requires static org.apache.httpcomponents.httpasyncclient;
    requires static org.apache.httpcomponents.httpcore.nio;
    requires static org.eclipse.jetty.client;

    // RESTEasy modules
    requires transitive org.jboss.resteasy.client.api;
    requires transitive org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.client.jaxrs.cache;
    exports org.jboss.resteasy.client.jaxrs.engine;
    exports org.jboss.resteasy.client.jaxrs.engines;
    exports org.jboss.resteasy.client.jaxrs.internal;
    exports org.jboss.resteasy.client.jaxrs.internal.proxy;
    exports org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;
    exports org.jboss.resteasy.client.jaxrs.spi;
    exports org.jboss.resteasy.plugins.providers.sse.client;

    // Service consumers
    uses org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
    uses org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

    // Provides
    provides jakarta.ws.rs.client.ClientBuilder with
        org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
    provides jakarta.ws.rs.sse.SseEventSource.Builder with
        org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
}