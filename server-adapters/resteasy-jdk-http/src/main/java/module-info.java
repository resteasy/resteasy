/**
 * RESTEasy Sun JDK HTTP Server Adapter module.
 * <p>
 * Provides an embedded HTTP server integration using the JDK's built-in
 * {@code com.sun.net.httpserver} package.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.server.jdk.http {

    // JDK modules
    requires jdk.httpserver;

    // Jakarta EE APIs
    requires jakarta.ws.rs;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;

    // Third-party dependencies
    requires static org.jboss.logging.annotations;
    requires org.jboss.logging;

    // Exports
    exports org.jboss.resteasy.plugins.server.sun.http;
}