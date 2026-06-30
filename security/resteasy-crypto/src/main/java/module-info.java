/**
 * RESTEasy Crypto module.
 * <p>
 * Provides security and cryptography features including S/MIME support,
 * digital signatures, and encryption/decryption for Jakarta REST.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.security.crypto {

    // JDK modules
    requires java.naming;
    requires java.security.jgss;

    // Jakarta EE APIs
    requires jakarta.mail;

    // Third-party dependencies
    requires org.bouncycastle.mail;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.provider;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.annotations.security.doseta;
    exports org.jboss.resteasy.security;
    exports org.jboss.resteasy.security.doseta;
    exports org.jboss.resteasy.security.smime;
}