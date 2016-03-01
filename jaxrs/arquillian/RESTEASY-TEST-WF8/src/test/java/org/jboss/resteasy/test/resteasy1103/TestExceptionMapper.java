package org.jboss.resteasy.test.resteasy1103;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ReaderException;

@Provider
public class TestExceptionMapper implements ExceptionMapper<ReaderException>
{
   @Override
   public Response toResponse(ReaderException exception)
   {  
      return Response.status(400).entity(exception.getMessage()).build();
   } 
}
