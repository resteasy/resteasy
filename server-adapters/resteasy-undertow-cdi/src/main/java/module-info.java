/**
 * RESTEasy Undertow CDI Adapter module.
 * <p>
 * Integrates RESTEasy with Undertow and CDI for embedded server deployments.
 * </p>
 *
 * @since 7.0.3
 */
module dev.resteasy.undertow.cdi {

    // Jakarta EE APIs
    requires jakarta.cdi;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires undertow.core;
    requires undertow.servlet;
    requires weld.api;
    requires weld.core.impl;
    requires weld.environment.common;
    requires weld.se.core;
    requires weld.servlet.core;
    requires weld.spi;
    requires xnio.api;

    // RESTEasy modules
    requires org.jboss.resteasy.cdi;
    requires org.jboss.resteasy.core;

    // Exports
    exports dev.resteasy.embedded.server;

    uses dev.resteasy.embedded.server.UndertowBuilderConfigurator;

    // Service providers
    provides org.jboss.resteasy.plugins.server.embedded.EmbeddedServer with
            dev.resteasy.embedded.server.UndertowCdiEmbeddedServer;
}
