/**
 * RESTEasy CDI Integration module.
 * <p>
 * Integrates RESTEasy with Jakarta Contexts and Dependency Injection (CDI),
 * enabling CDI bean discovery and injection in Jakarta REST resources.
 * </p>
 *
 * @since 7.0.3
 */
module org.jboss.resteasy.cdi {
    // JDK modules
    requires java.naming;

    // Jakarta EE APIs
    requires jakarta.cdi;
    requires jakarta.inject;
    requires jakarta.interceptor;
    requires jakarta.servlet;
    requires jakarta.ws.rs;
    requires static jakarta.ejb;

    // Third-party dependencies
    requires org.jboss.jandex;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    // Used if Weld is available, but may not be required in some instances
    requires static weld.core.impl;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.cdi;

    // Opens
    opens org.jboss.resteasy.cdi to weld.core.impl;

    // Service providers
    // CDI extension discovery
    provides jakarta.enterprise.inject.spi.Extension
            with org.jboss.resteasy.cdi.ResteasyCdiExtension;
}