package org.jboss.resteasy.core.interception.jaxrs;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Suspendable request context, which allows the users to suspend execution of the filter
 * chain until it is resumed normally, or abnormally with a {@link Throwable}. 
 *
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public interface SuspendableContainerRequestContext extends ContainerRequestContext
{
   /**
    * Suspends the current request. This makes the current request asynchronous. No
    * further request filter is executed until this request is resumed.
    * 
    * No reply is going to be sent to the client until this request is resumed either
    * with {@link #resume()} or aborted with {@link #resume(Throwable)} or 
    * {@link #abortWith(javax.ws.rs.core.Response)}.
    */
   void suspend();
   
   /**
    * Resumes the current request, and proceeds to the next request filter, if any,
    * or to the resource method.
    */
   void resume();
   
   /**
    * Aborts the current request with the given exception. This behaves as if the request
    * filter threw this exception synchronously, which means exceptions may be mapped via
    * exception mappers, response filters will run and async response callbacks will be
    * called with this exception.
    * 
    * @param t the exception to send back to the client, as mapped by the application.
    */
   void resume(Throwable t);
}
