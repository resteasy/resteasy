package org.jboss.resteasy.plugins.server.reactor.netty;

/**
 * @deprecated use the new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class ReactorNettySendException extends RuntimeException {

    public ReactorNettySendException(final Throwable cause) {
        super(cause);
    }
}
