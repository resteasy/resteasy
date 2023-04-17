package org.jboss.resteasy.jose.jws;

import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jose.jws.crypto.SignatureProvider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public enum Algorithm {

    none(null, null),
    HS256(AlgorithmType.HMAC, null),
    HS384(AlgorithmType.HMAC, null),
    HS512(AlgorithmType.HMAC, null),
    RS256(AlgorithmType.RSA, new RSAProvider()),
    RS384(AlgorithmType.RSA, new RSAProvider()),
    RS512(AlgorithmType.RSA, new RSAProvider()),
    PS256(AlgorithmType.RSA, null),
    PS384(AlgorithmType.RSA, null),
    PS512(AlgorithmType.RSA, null),
    ES256(AlgorithmType.ECDSA, null),
    ES384(AlgorithmType.ECDSA, null),
    ES512(AlgorithmType.ECDSA, null);

    private AlgorithmType type;
    private SignatureProvider provider;

    Algorithm(final AlgorithmType type, final SignatureProvider provider) {
        this.type = type;
        this.provider = provider;
    }

    public AlgorithmType getType() {
        return type;
    }

    public SignatureProvider getProvider() {
        return provider;
    }
}
