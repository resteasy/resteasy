/**
 * RESTEasy Servlet Initializer module.
 * <p>
 * Provides servlet container initialization for automatically registering RESTEasy
 * in Jakarta Servlet environments via ServletContainerInitializer.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.servlet.initializer {

    // Jakarta EE APIs
    requires jakarta.servlet;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.servlet;

    // Provides
    provides jakarta.servlet.ServletContainerInitializer
            with org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
}
