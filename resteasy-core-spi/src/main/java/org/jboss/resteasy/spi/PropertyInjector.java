package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface PropertyInjector
{
   /**
    * Inject values into annotated properties (fields/setter methods) of the target object.
    * This method should only be used outside the scope of an HTTP request.
    *
    * @param target target object
    * @param unwrapAsync unwrap async
    * @return {@link CompletionStage} or null if async isn't needed
    */
   CompletionStage<Void> inject(Object target, boolean unwrapAsync);

   /**
    * Inject values into annotated properties (fields/setter methods) of the target object.
    * This method should only be used inside the scope of an HTTP request.
    *
    * @param request http request
    * @param response http response
    * @param target target object
    * @param unwrapAsync unwrap async
    * @return {@link CompletionStage} or null if async isn't needed
    * @throws Failure if application failure occurred
    * @throws WebApplicationException if application exception occurred
    */
   CompletionStage<Void> inject(HttpRequest request, HttpResponse response, Object target, boolean unwrapAsync) throws Failure, WebApplicationException, ApplicationException;
}
