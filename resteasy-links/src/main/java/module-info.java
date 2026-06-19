/**
 * RESTEasy Links module.
 * <p>
 * Provides HATEOAS (Hypermedia as the Engine of Application State) link support
 * for building RESTful APIs with hypermedia controls.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.links {
    // JDK modules
    requires java.desktop;

    // Jakarta EE APIs
    requires jakarta.el;
    requires jakarta.persistence;
    requires transitive jakarta.xml.bind;

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires static org.glassfish.jaxb.runtime;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.plugins.providers.jackson;

    // Exports
    exports org.jboss.resteasy.links;

    // Opens
    opens org.jboss.resteasy.links to
            org.jboss.resteasy.core,
            com.fasterxml.jackson.databind,
            jakarta.xml.bind,
            org.glassfish.jaxb.runtime;
    opens org.jboss.resteasy.links.impl to
            org.jboss.resteasy.core;
}
