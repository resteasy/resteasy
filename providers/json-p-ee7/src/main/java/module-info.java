/**
 * RESTEasy JSON-P Providers module.
 * <p>
 * Provides Jakarta JSON Processing (JSON-P) message body readers and writers.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.plugins.providers.json {

    // Jakarta EE APIs
    requires jakarta.json;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.providers.jsonp;

    // Opens
    opens org.jboss.resteasy.plugins.providers.jsonp to org.jboss.resteasy.core;
}