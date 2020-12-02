package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AsyncResponseExceptionMapper implements ExceptionMapper<AsyncResponseException>
{

   @Override
   public Response toResponse(AsyncResponseException exception)
   {
      return Response.ok("Got it").status(444).build();
   }

}
