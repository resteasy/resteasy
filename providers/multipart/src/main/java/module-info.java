
/**
 * RESTEasy Multipart Providers module.
 * <p>
 * Provides multipart/form-data and multipart/related message body readers and writers.
 * Supports file uploads and complex multipart content.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.plugins.providers.multipart {

    // JDK modules
    // Required for java.bean
    requires java.desktop;

    // Jakarta EE APIs
    requires jakarta.mail;
    requires jakarta.ws.rs;

    // Third-party dependencies
    requires apache.mime4j.core;
    requires apache.mime4j.dom;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires static apache.mime4j.storage;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.plugins.providers.jaxb;

    // Exports
    exports org.jboss.resteasy.annotations.providers.multipart;
    exports org.jboss.resteasy.plugins.providers.multipart;

    // Opens
    opens org.jboss.resteasy.plugins.providers.multipart to org.jboss.resteasy.core;
}
