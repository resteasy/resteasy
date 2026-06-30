/**
 * RESTEasy FastInfoset Providers module.
 * <p>
 * Provides FastInfoset binary XML format message body readers and writers.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.plugins.providers.fastinfoset {

    // Jakarta EE APIs
    requires jakarta.xml.bind;

    // Third-party dependencies
    requires com.sun.xml.fastinfoset;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.spi;
    requires org.jboss.resteasy.plugins.providers.jaxb;

    // Exports
    exports org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;
}
