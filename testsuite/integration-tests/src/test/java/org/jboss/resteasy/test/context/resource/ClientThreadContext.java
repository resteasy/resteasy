package org.jboss.resteasy.test.context.resource;

import org.jboss.resteasy.spi.concurrent.ThreadContext;

/**
 * A {@link ThreadContext} which is only registered on a client. It is intentionally not annotated with
 * {@code @Provider} so it's never discovered and registered on the server.
 *
 * @since 8.0
 */
public class ClientThreadContext implements ThreadContext<String> {

    @Override
    public String capture() {
        return "captured";
    }

    @Override
    public void push(final String context) {
    }

    @Override
    public void reset(final String context) {
    }
}
