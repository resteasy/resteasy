/**
 * RESTEasy JSON-B Providers module.
 * <p>
 * Provides Jakarta JSON Binding (JSON-B) message body readers and writers.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.plugins.providers.json.bind {
    // Jakarta EE APIs
    requires jakarta.json;
    requires jakarta.json.bind;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;
    requires static org.jboss.resteasy.tracing.api;

    // Exports
    exports org.jboss.resteasy.plugins.providers.jsonb;
    exports org.jboss.resteasy.tracing.providers.jsonb;

    opens org.jboss.resteasy.plugins.providers.jsonb to org.jboss.resteasy.core;

    // Provides
    provides org.jboss.resteasy.tracing.api.RESTEasyTracingInfo with
            org.jboss.resteasy.tracing.providers.jsonb.JSONBJsonFormatRESTEasyTracingInfo;
}