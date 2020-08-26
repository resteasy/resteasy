package org.jboss.resteasy.jose.jws.crypto;

import org.jboss.resteasy.jose.jws.JWSInput;

public interface SignatureProvider {
    boolean verify(JWSInput input, String key);
}
