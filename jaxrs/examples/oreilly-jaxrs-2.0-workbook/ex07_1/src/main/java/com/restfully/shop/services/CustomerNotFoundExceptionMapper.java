package com.restfully.shop.services;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class CustomerNotFoundExceptionMapper implements ExceptionMapper<CustomerNotFoundException>
{
   public Response toResponse(CustomerNotFoundException exception)
   {
      return Response.status(Response.Status.NOT_FOUND)
              .entity(exception.getMessage())
              .type("text/plain").build();
   }
}
