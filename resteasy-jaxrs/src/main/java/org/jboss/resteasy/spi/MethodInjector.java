package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.core.ValueInjector;

/**
 * Will invoke a method in the context of an HTTP request.  Does all the parameter injection for you.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MethodInjector
{
   /**
    * Invoke on a method in the context of an HTTP request.  Does all JAX-RS parameter injection.
    *
    * @param request
    * @param response
    * @param target
    * @return
    * @throws Failure
    */
   CompletionStage<Object> invoke(HttpRequest request, HttpResponse response, Object target) throws Failure, ApplicationException;

   /**
    * Create the arguments that would be used to invoke the method in the context of an HTTP request.
    *
    * @param request
    * @param response
    * @return
    * @throws Failure
    */
   CompletionStage<Object[]> injectArguments(HttpRequest request, HttpResponse response) throws Failure;

   ValueInjector[] getParams();

   boolean expectsBody();
}
