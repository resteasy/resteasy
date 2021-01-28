package org.jboss.resteasy.microprofile.client.header;

/**
 * Thrown on errors in generating header values
 */
public class ClientHeaderFillingException extends RuntimeException {
    public ClientHeaderFillingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
