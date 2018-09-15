package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ValueInjector
{
   /**
    * Inject outside the context of an HTTP request.  For instance, a singleton may have proxiable and injectable
    * jax-rs objects like Request, UriInfo, or HttpHeaders.
    *
    * @return object
    */
   CompletionStage<Object> inject(boolean unwrapAsync);

   /**
    * Inject inside the context of an HTTP request.
    *
    * @param request http request
    * @param response http response
    * @return object
    */
   CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync);
}
