/**
 * RESTEasy Client Jetty Engine module.
 * <p>
 * This module provides the Eclipse Jetty HTTP client engine implementation for RESTEasy.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.client.jetty {
    // Third-party dependencies
    requires org.eclipse.jetty.client;
    requires org.jboss.logging;

    // RESTEasy modules
    requires transitive org.jboss.resteasy.client;

    // Jakarta APIs
    requires jakarta.servlet;
    requires jakarta.ws.rs;

    // Exports
    exports org.jboss.resteasy.client.jaxrs.engines.jetty;

    // Service provider
    provides org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory with
        org.jboss.resteasy.client.jaxrs.engines.jetty.JettyHttpClientEngineFactory;
}
