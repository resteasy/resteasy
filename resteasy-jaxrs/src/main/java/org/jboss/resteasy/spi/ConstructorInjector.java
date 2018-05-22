package org.jboss.resteasy.spi;

import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ConstructorInjector
{
   /**
    * Construct outside the scope of an HTTP request.  Useful for singleton factories.
    *
    * @return constructed object
    */
   Object construct();

   /**
    * Construct inside the scope of an HTTP request.
    *
    * @param request http request
    * @param response http response
    * @return constructed object
    * @throws Failure if failure occurred
    * @throws WebApplicationException if application exception occurred
    * @throws ApplicationException if application exception occurred
    */
   Object construct(HttpRequest request, HttpResponse response) throws Failure, WebApplicationException, ApplicationException;

   /**
    * Create an arguments list from injectable tings outside the scope of an HTTP request.  Useful for singleton factories
    * in cases where the resource factory wants to allocate the object itself, but wants resteasy to populate
    * the arguments.
    *
    * @return array of arguments
    */
   Object[] injectableArguments();

   /**
    * Create an argument list inside the scope of an HTTP request.
    * Useful in cases where the resource factory wants to allocate the object itself, but wants resteasy to populate
    * the arguments.
    *
    * @param request http request
    * @param response http response
    * @return array of arguments
    * @throws Failure if failure occurred
    */
   Object[] injectableArguments(HttpRequest request, HttpResponse response) throws Failure;
}
