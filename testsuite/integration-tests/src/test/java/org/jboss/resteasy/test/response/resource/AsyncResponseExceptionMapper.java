package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AsyncResponseExceptionMapper implements ExceptionMapper<AsyncResponseException>
{

   @Override
   public Response toResponse(AsyncResponseException exception)
   {
      return Response.ok("Got it").status(444).build();
   }

}
