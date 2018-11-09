package org.jboss.resteasy.test.mapper.resource;

import org.jboss.resteasy.spi.ReaderException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

   // This mapper forces a 204 status code by ExceptionHandler
   public Response toResponse(IOException e) {
      return null;
   }
}
