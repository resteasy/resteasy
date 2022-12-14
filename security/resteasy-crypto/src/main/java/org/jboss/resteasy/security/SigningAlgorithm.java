package org.jboss.resteasy.security;

/*
 * Allowed signing algorithms by DKIM RFC 4871 with translation to different Java notations
 *
 * @author Florian Sager, http://www.agitos.de, 22.11.2008
 */

public class SigningAlgorithm {

    public static SigningAlgorithm SHA256withRSA = new SigningAlgorithm("rsa-sha256", "SHA256withRSA", "sha-256");
    public static SigningAlgorithm SHA1withRSA = new SigningAlgorithm("rsa-sha1", "SHA1withRSA", "sha-1");

    private String rfc4871Notation;
    private String javaSecNotation;
    private String javaHashNotation;

    // 1. argument: RFC 4871 format, 2. argument: java representation, 3. argument: java hashing digest
    public SigningAlgorithm(final String rfc4871Notation, final String javaSecNotation, final String javaHashNotation) {
        this.rfc4871Notation = rfc4871Notation;
        this.javaSecNotation = javaSecNotation;
        this.javaHashNotation = javaHashNotation;
    }

    public String getJavaHashNotation() {
        return javaHashNotation;
    }

    public String getJavaSecNotation() {
        return javaSecNotation;
    }

    public String getRfcNotation() {
        return rfc4871Notation;
    }
}
