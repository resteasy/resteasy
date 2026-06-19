/**
 * RESTEasy Jackson 2 Providers module.
 * <p>
 * Provides Jackson 2.x message body readers and writers for JSON content type handling.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.plugins.providers.jackson {

    // Third-party dependencies
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jakarta.rs.base;
    requires com.fasterxml.jackson.jakarta.rs.json;
    requires json.patch;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires static com.fasterxml.jackson.datatype.jdk8;
    requires static com.fasterxml.jackson.datatype.jsr310;
    requires static com.fasterxml.jackson.module.jakarta.xmlbind;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires static org.jboss.resteasy.tracing.api;

    // Exports
    exports org.jboss.resteasy.annotations.providers.jackson;
    exports org.jboss.resteasy.plugins.providers.jackson;
    exports org.jboss.resteasy.tracing.providers.jackson2;

    // Opens
    opens org.jboss.resteasy.plugins.providers.jackson to org.jboss.resteasy.core;

    // Provides
    provides org.jboss.resteasy.tracing.api.RESTEasyTracingInfo with
            org.jboss.resteasy.tracing.providers.jackson2.Jackson2JsonFormatRESTEasyTracingInfo;
}
