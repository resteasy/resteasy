package org.jboss.resteasy.security.smime;

import java.security.PrivateKey;

import jakarta.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SignedOutput extends SMIMEOutput {
    protected PrivateKey privateKey;

    public SignedOutput(final Object obj, final String mediaType) {
        super(obj, mediaType);
    }

    public SignedOutput(final Object obj, final MediaType mediaType) {
        super(obj, mediaType);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
