package org.jboss.resteasy.test.exception.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperInjectionNotFoundMapper implements
      ExceptionMapper<NotFoundException> {
   private static Logger logger = Logger.getLogger(ExceptionMapperInjectionNotFoundMapper.class);

   @Context
   HttpHeaders httpHeaders;

   public Response toResponse(NotFoundException exception) {
      logger.info(String.format("Request headers: %s", httpHeaders.getRequestHeaders()));
      logger.info("Exception is mapped!");
      return Response.status(HttpResponseCodes.SC_HTTP_VERSION_NOT_SUPPORTED).build();
   }
}
