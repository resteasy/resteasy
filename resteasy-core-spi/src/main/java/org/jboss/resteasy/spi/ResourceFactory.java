package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

/**
 * Implementations of this interface are registered through the Registry class.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceFactory
{
   /**
    * Class to scan for jax-rs annotations
    *
    * @return class
    */
   Class<?> getScannableClass();

   /**
    * Callback after registration has been completed.
    *
    * @param factory allows singleton factories to pre-inject things like @Context references into the singleton instance
    */
   void registered(ResteasyProviderFactory factory);

   /**
    * Called per request to obtain a resource instance to invoke http request on.
    *
    * @param request http request
    * @param response http response
    * @param factory provider factory
    * @return resource
    */
   CompletionStage<Object> createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory);


   /**
    * Callback when request is finished.  usable for things like @PreDestroy if the underlying factory supports it
    *
    * @param request http request
    * @param response http response
    * @param resource resource
    */
   void requestFinished(HttpRequest request, HttpResponse response, Object resource);

   void unregistered();
}
