package org.jboss.resteasy.spi;

import javax.ws.rs.WebApplicationException;

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
    * @param target
    */
   void inject(Object target);

   /**
    * Inject values into annotated properties (fields/setter methods) of the target object.
    * This method should only be used inside the scope of an HTTP request.
    *
    * @param request
    * @param response
    * @param target
    * @throws Failure
    */
   void inject(HttpRequest request, HttpResponse response, Object target) throws Failure, WebApplicationException, ApplicationException;
}
