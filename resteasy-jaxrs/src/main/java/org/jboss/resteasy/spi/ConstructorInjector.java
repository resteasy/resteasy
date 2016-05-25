package org.jboss.resteasy.spi;

import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ConstructorInjector
{
   /**
    * construct outside the scope of an HTTP request.  Useful for singleton factories
    *
    * @return
    */
   Object construct();

   /**
    * construct inside the scope of an HTTP request.
    *
    * @param request
    * @param response
    * @return
    * @throws Failure
    */
   Object construct(HttpRequest request, HttpResponse response) throws Failure, WebApplicationException, ApplicationException;

   /**
    * Create an arguments list from injectable tings outside the scope of an HTTP request.  Useful for singleton factories
    * in cases where the resource factory wants to allocate the object itself, but wants resteasy to populate
    * the arguments
    *
    * @return
    */
   Object[] injectableArguments();

   /**
    * Create an argument list inside the scope of an HTTP request.
    * Useful in cases where the resource factory wants to allocate the object itself, but wants resteasy to populate
    * the arguments
    *
    * @param request
    * @param response
    * @return
    * @throws Failure
    */
   Object[] injectableArguments(HttpRequest request, HttpResponse response) throws Failure;
}
