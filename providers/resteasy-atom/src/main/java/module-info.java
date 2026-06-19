/**
 * RESTEasy Atom Providers module.
 * <p>
 * Provides support for Atom syndication feeds (RFC 4287).
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.plugins.providers.atom {

    // Jakarta EE APIs
    requires jakarta.xml.bind;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires org.glassfish.jaxb.runtime;
    requires static org.glassfish.jaxb.core;
    requires static org.glassfish.jaxb.xjc;
    requires static com.sun.tools.rngdatatype;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.spi;
    requires org.jboss.resteasy.plugins.providers.jaxb;

    // Exports
    exports org.jboss.resteasy.plugins.providers.atom;
    exports org.jboss.resteasy.plugins.providers.atom.app;

    // Opens
    opens org.jboss.resteasy.plugins.providers.atom to org.jboss.resteasy.core;
}
