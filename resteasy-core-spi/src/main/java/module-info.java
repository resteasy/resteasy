/**
 * RESTEasy Core SPI module.
 * <p>
 * This module provides the Service Provider Interface (SPI) for RESTEasy,
 * including core abstractions, annotations, and extension points.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.spi {
    // JDK modules
    requires java.desktop;
    requires java.naming;

    // Jakarta EE APIs
    requires transitive jakarta.activation;
    requires transitive jakarta.annotation;
    requires transitive jakarta.validation;
    requires transitive jakarta.ws.rs;
    requires transitive jakarta.xml.bind;

    // Third-party dependencies
    requires org.reactivestreams;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires static org.jboss.resteasy.tracing.api;

    // Exports
    exports org.jboss.resteasy.annotations;
    exports org.jboss.resteasy.annotations.cache;
    exports org.jboss.resteasy.annotations.jaxrs;
    exports org.jboss.resteasy.annotations.providers.img;
    exports org.jboss.resteasy.api.validation;
    exports org.jboss.resteasy.concurrent;
    exports org.jboss.resteasy.resteasy_jaxrs.i18n;
    exports org.jboss.resteasy.spi;
    exports org.jboss.resteasy.spi.concurrent;
    exports org.jboss.resteasy.spi.config;
    exports org.jboss.resteasy.spi.interception;
    exports org.jboss.resteasy.spi.metadata;
    exports org.jboss.resteasy.spi.multipart;
    exports org.jboss.resteasy.spi.statistics;
    exports org.jboss.resteasy.spi.touri;
    exports org.jboss.resteasy.spi.util;
    exports org.jboss.resteasy.spi.validation;

    // Service consumers
    uses jakarta.ws.rs.ext.Providers;
    uses org.jboss.resteasy.spi.Registry;
    uses org.jboss.resteasy.spi.concurrent.ThreadContext;
    uses org.jboss.resteasy.spi.config.ConfigurationFactory;
}
