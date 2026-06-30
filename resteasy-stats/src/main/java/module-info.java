/**
 * RESTEasy Statistics module.
 * <p>
 * Provides statistics collection and monitoring for RESTEasy applications.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.stats {

    // Jakarta EE APIs
    requires jakarta.xml.bind;

    // Third-party dependencies
    requires org.jboss.logging;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.stats;
}
