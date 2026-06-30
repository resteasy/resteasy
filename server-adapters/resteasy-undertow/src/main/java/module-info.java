/**
 * RESTEasy Undertow Adapter module.
 * <p>
 * Integrates RESTEasy with JBoss Undertow web server.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.undertow {

    // Jakarta EE APIs
    requires jakarta.servlet;

    // Third-party dependencies
    requires undertow.core;
    requires undertow.servlet;
    requires xnio.api;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.server.undertow;

    // Service providers
    provides org.jboss.resteasy.plugins.server.embedded.EmbeddedServer with
        org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
}