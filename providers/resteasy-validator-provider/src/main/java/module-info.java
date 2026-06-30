/**
 * RESTEasy Validator Provider module.
 * <p>
 * Integrates Jakarta Bean Validation with RESTEasy for automatic request/response validation.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.plugins.validation {
    // JDK modules
    requires java.naming;

    // Jakarta EE APIs
    requires jakarta.cdi;
    requires jakarta.validation;

    // Third-party dependencies
    requires com.fasterxml.classmate;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.cdi;
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.plugins.validation;
}
