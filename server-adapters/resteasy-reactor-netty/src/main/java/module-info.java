/**
 * RESTEasy Reactor Netty Server Adapter module.
 * <p>
 * This module provides the Reactor Netty embedded server integration for RESTEasy.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.reactor.netty {
    // Third-party dependencies
    requires io.netty.buffer;
    requires io.netty.codec.http;
    requires io.netty.codec.http2;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires io.netty.resolver;
    requires io.netty.resolver.dns;
    requires io.netty.transport;
    requires reactor.netty;
    requires reactor.netty.core;
    requires reactor.netty.http;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // Jakarta APIs
    requires jakarta.servlet;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;
    requires org.jboss.resteasy.reactor;

    // Exports
    exports org.jboss.resteasy.plugins.server.reactor.netty;
}
