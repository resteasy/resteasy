package org.jboss.resteasy.core.interception.jaxrs;

import jakarta.ws.rs.container.ContainerResponseContext;

/**
 * Suspendable response context, which allows the users to suspend execution of the filter
 * chain until it is resumed normally, or abnormally with a {@link Throwable}.
 *
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public interface SuspendableContainerResponseContext extends ContainerResponseContext {
    /**
     * Suspends the current response. This makes the current request asynchronous. No
     * further response filter is executed until this response is resumed.
     *
     * No reply is going to be sent to the client until this response is resumed either
     * with {@link #resume()} or aborted with {@link #resume(Throwable)} or
     * {@link ResponseContainerRequestContext#abortWith(jakarta.ws.rs.core.Response)}.
     */
    void suspend();

    /**
     * Resumes the current response, and proceeds to the next response filter, if any,
     * or to send the response.
     */
    void resume();

    /**
     * Aborts the current response with the given exception. This behaves as if the request
     * filter threw this exception synchronously, which means that the exception will not
     * be mapped by exception mappers, the response filters will stop running, and the
     * async response callbacks will be called with this exception.
     *
     * @param t the exception to send back to the client, as an internal server error.
     */
    void resume(Throwable t);

}
