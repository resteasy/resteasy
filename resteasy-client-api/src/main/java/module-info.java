/**
 * RESTEasy Client API module.
 * <p>
 * This module provides the client-side API extensions for Jakarta RESTful Web Services,
 * including client configuration, async invocation support, and exception handling.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.client.api {

    // Third-party dependencies
    requires org.jboss.logging;
    requires org.reactivestreams;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires transitive org.jboss.resteasy.spi;

    // Exports
    exports org.jboss.resteasy.client.exception;
    exports org.jboss.resteasy.client.jaxrs;
    exports org.jboss.resteasy.client.jaxrs.api;
    exports org.jboss.resteasy.client.jaxrs.i18n;

    // Service consumers
    uses org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
    uses org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
    uses org.jboss.resteasy.client.jaxrs.ClientHttpEngineBuilder;
    uses org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

}