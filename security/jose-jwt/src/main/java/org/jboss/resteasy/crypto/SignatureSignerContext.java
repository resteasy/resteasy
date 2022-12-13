package org.jboss.resteasy.crypto;

import java.security.SignatureException;

public interface SignatureSignerContext {
    String getKid();

    String getAlgorithm();

    String getHashAlgorithm();

    byte[] sign(byte[] data) throws SignatureException;
}
