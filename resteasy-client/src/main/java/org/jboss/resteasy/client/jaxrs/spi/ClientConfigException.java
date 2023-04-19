package org.jboss.resteasy.client.jaxrs.spi;

/**
 * General client configuration exception
 *
 * @author dvilkola@redhat.com
 */
public class ClientConfigException extends RuntimeException {
    public ClientConfigException() {
        super();
    }

    public ClientConfigException(final String message) {
        super(message);
    }

    public ClientConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClientConfigException(final Throwable cause) {
        super(cause);
    }
}
