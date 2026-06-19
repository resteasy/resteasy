/**
 * RESTEasy Client Utilities module.
 * <p>
 * Provides utility classes for Jakarta REST client applications.
 * </p>
 *
 * @since 6.2.17
 */
module dev.resteasy.client.util {

    // Jakarta EE APIs
    requires transitive jakarta.ws.rs;
    requires jakarta.annotation;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // Exports
    exports dev.resteasy.client.util.authentication;
    exports dev.resteasy.client.util.authentication.basic;
    exports dev.resteasy.client.util.authentication.digest;
}
