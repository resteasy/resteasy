/**
 * RESTEasy JavaScript API module.
 * <p>
 * Generates JavaScript client stubs for Jakarta REST resources.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.jsapi {

    // Jakarta EE APIs
    requires jakarta.servlet;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.jsapi;
    exports org.jboss.resteasy.jsapi.i18n;
}
