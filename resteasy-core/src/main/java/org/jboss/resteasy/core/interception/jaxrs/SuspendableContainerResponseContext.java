package org.jboss.resteasy.core.interception.jaxrs;

import jakarta.ws.rs.container.ContainerResponseContext;

/**
 * Suspendable response context, which allows the users to suspend execution of the filter
 * chain until it is resumed normally, or abnormally with a {@link Throwable}.
 *
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 * @deprecated resuming a suspended filter chain may continue execution on a different thread than the one that
 *             suspended it. CDI contexts are bound to a specific thread, so CDI may not behave as expected after
 *             such a resumption: a {@code @RequestScoped}, {@code @SessionScoped}, or {@code @ConversationScoped}
 *             bean may fail to resolve, or resolve to a different instance than expected, regardless of what scope
 *             the matched resource itself uses. {@code @ApplicationScoped} and {@code @Dependent} beans are
 *             unaffected. This is consistent with the Jakarta Concurrency specification's own guidance against
 *             relying on those same CDI scopes surviving an asynchronous task boundary, and reliably fixing it is
 *             not something this module can do in a CDI-implementation-agnostic way. Suspending the filter chain
 *             itself is deprecated for removal; asynchronous processing in resource methods via
 *             {@code AsyncResponse}/{@code CompletionStage} is not deprecated and remains fully supported as a
 *             mechanism, but is equally susceptible to this same caveat - code that doesn't reference any CDI bean
 *             after dispatching to another thread is unaffected, but code that does is not guaranteed to work.
 */
@Deprecated(forRemoval = true, since = "8.0")
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

    // TODO (jrp) add @Override
    //@Override
    default String getHeaderString(String name, String separator) {
        // TODO (jrp) implement
        throw new UnsupportedOperationException();
    }

}
