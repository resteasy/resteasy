/**
 * RESTEasy Vert.x Server Adapter module.
 * <p>
 * This module provides the Vert.x embedded server integration for RESTEasy.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.vertx {
    // Jakarta APIs
    requires jakarta.ws.rs;

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
    requires static io.netty.transport.classes.epoll;
    requires static io.netty.transport.classes.kqueue;
    requires static io.vertx.core;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;

    // Exports
    exports org.jboss.resteasy.plugins.server.vertx;
}
