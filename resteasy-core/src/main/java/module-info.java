/**
 * RESTEasy Core module.
 * <p>
 * This module provides the core implementation of Jakarta RESTful Web Services,
 * including resource scanning, method invocation, provider handling, and
 * built-in message body readers/writers.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.core {
    // JDK modules
    requires java.desktop;  // For image/media type handling
    requires java.management;  // For statistics/monitoring
    requires java.naming;  // For JNDI lookups
    requires java.xml;  // For XML processing

    // Jakarta EE APIs
    requires transitive jakarta.servlet;
    requires jakarta.activation;
    requires jakarta.annotation;
    requires jakarta.ws.rs;
    requires jakarta.xml.bind;

    // Third-party dependencies
    requires org.eclipse.angus.activation;
    requires org.jboss.logging;
    requires org.reactivestreams;
    requires static org.jboss.resteasy.tracing.api;
    requires transitive org.jboss.jandex;

    // RESTEasy modules
    requires transitive org.jboss.resteasy.spi;

    // Exports
    exports org.jboss.resteasy.core;
    exports org.jboss.resteasy.core.interception.jaxrs;
    exports org.jboss.resteasy.core.messagebody;
    exports org.jboss.resteasy.core.providerfactory;
    exports org.jboss.resteasy.core.registry;
    exports org.jboss.resteasy.core.se;
    exports org.jboss.resteasy.plugins.delegates;
    exports org.jboss.resteasy.plugins.interceptors;
    exports org.jboss.resteasy.plugins.providers;
    exports org.jboss.resteasy.plugins.providers.sse;
    exports org.jboss.resteasy.plugins.server;
    exports org.jboss.resteasy.plugins.server.embedded;
    exports org.jboss.resteasy.plugins.server.resourcefactory;
    exports org.jboss.resteasy.plugins.server.servlet;
    exports org.jboss.resteasy.specimpl;
    exports org.jboss.resteasy.statistics;
    exports org.jboss.resteasy.tracing;
    exports org.jboss.resteasy.util;
    exports org.jboss.resteasy.core.extractors;

    // Service consumers
    // Note: Registry and EntityPart.Builder use PriorityServiceLoader with custom constructors,
    // so we only declare 'uses' - implementations are discovered via META-INF/services
    uses jakarta.ws.rs.container.DynamicFeature;
    uses jakarta.ws.rs.core.EntityPart.Builder;
    uses jakarta.ws.rs.core.Feature;
    uses org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
    uses org.jboss.resteasy.spi.Registry;

    // Provides
    provides jakarta.ws.rs.ext.Providers with
        org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
    provides jakarta.ws.rs.ext.RuntimeDelegate with
        org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
    provides org.jboss.resteasy.spi.concurrent.ThreadContext with
        org.jboss.resteasy.core.concurrent.ResteasyThreadContext;
}
