package org.jboss.resteasy.core;

import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

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
    * @return
    */
   CompletionStage<Object> inject();

   /**
    * Inject inside the context of an HTTP request.
    *
    * @param request
    * @param response
    * @return
    */
   CompletionStage<Object> inject(HttpRequest request, HttpResponse response);
}
