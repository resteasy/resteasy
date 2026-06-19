/**
 * RESTEasy Netty 4 CDI Server Adapter module.
 * <p>
 * This module provides the Netty 4 embedded server integration with CDI support for RESTEasy.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.netty.cdi {
    // Jakarta APIs
    requires jakarta.cdi;
    requires jakarta.interceptor;
    requires jakarta.servlet;
    requires jakarta.ws.rs;

    // Third-party dependencies
    requires weld.api;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;
    requires transitive org.jboss.resteasy.netty;

    // Exports
    exports org.jboss.resteasy.plugins.server.netty.cdi;
}
