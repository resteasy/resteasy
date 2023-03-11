package org.jboss.resteasy.security.doseta;

import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerificationResult {
    protected boolean verified;
    protected Exception failureException;
    protected DKIMSignature signature;
    protected MultivaluedMap<String, String> verifiedHeaders = new MultivaluedMapImpl<String, String>();

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Exception getFailureException() {
        return failureException;
    }

    public void setFailureException(Exception failureException) {
        this.failureException = failureException;
    }

    public DKIMSignature getSignature() {
        return signature;
    }

    public void setSignature(DKIMSignature signature) {
        this.signature = signature;
    }

    public MultivaluedMap<String, String> getVerifiedHeaders() {
        return verifiedHeaders;
    }

    public void setVerifiedHeaders(MultivaluedMap<String, String> verifiedHeaders) {
        this.verifiedHeaders = verifiedHeaders;
    }
}
