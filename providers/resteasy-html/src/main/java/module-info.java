/**
 * RESTEasy HTML Providers module.
 * <p>
 * Provides HTML message body readers and writers.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.plugins.providers.html {

    // Jakarta EE APIs
    requires jakarta.servlet;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.providers.html;
}
