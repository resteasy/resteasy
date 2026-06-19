/**
 * RESTEasy Netty 4 Server Adapter module.
 * <p>
 * This module provides the Netty 4 embedded server integration for RESTEasy.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.netty {
    // Jakarta APIs
    requires jakarta.ws.rs;

    // Third-party dependencies
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.codec.http;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires io.netty.resolver;
    requires io.netty.resolver.dns;
    requires io.netty.transport;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;

    // Exports
    exports org.jboss.resteasy.plugins.server.netty;
}
