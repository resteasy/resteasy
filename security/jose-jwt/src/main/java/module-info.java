/**
 * RESTEasy JOSE JWT module.
 * <p>
 * Provides JSON Object Signing and Encryption (JOSE) and JSON Web Token (JWT)
 * support for authentication and authorization.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.security.jose.jwt {

    // Third-party dependencies
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.provider;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    requires static jcip.annotations;

    // RESTEasy modules
    requires transitive org.jboss.resteasy.plugins.providers.jackson;
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.jose.jwe;
    exports org.jboss.resteasy.jose.jwe.crypto;
    exports org.jboss.resteasy.jose.jws;
    exports org.jboss.resteasy.jose.jws.crypto;
    exports org.jboss.resteasy.jwt;
}
