/**
 * RESTEasy JAXB Provider module.
 * <p>
 * Provides JAXB (Jakarta XML Binding) message body readers and writers
 * for XML content type handling.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.plugins.providers.jaxb {

    // Jakarta EE APIs
    requires jakarta.annotation;
    requires jakarta.xml.bind;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // JAXB implementation modules
    requires org.glassfish.jaxb.runtime;

    // Defined as a hard dependency in org.glassfish.jaxb.xjc
    requires static com.sun.tools.rngdatatype;
    requires static com.sun.xml.dtdparser;
    requires static org.glassfish.jaxb.core;
    requires static org.glassfish.jaxb.xjc;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.annotations.providers.jaxb;
    exports org.jboss.resteasy.plugins.providers.jaxb;

    // Opens
    opens org.jboss.resteasy.plugins.providers.jaxb to org.jboss.resteasy.core;
}