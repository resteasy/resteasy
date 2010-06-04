package org.jboss.resteasy.test.application;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class FooExceptionMapper implements ExceptionMapper<FooException>
{
   public Response toResponse(FooException exception)
   {
      return Response.status(412).build();
   }
}
