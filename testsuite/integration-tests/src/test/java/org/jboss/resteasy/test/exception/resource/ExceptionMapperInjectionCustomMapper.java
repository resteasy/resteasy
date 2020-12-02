package org.jboss.resteasy.test.exception.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;

public class ExceptionMapperInjectionCustomMapper implements ExceptionMapper<ExceptionMapperCustomRuntimeException> {

   private static Logger logger = Logger.getLogger(ExceptionMapperInjectionCustomMapper.class);

   @Context
   Request request;

   public Response toResponse(ExceptionMapperCustomRuntimeException exception) {
      logger.info("Method: " + request.getMethod());

      ArrayList<Variant> list = new ArrayList<Variant>();
      list.add(new Variant(MediaType.APPLICATION_JSON_TYPE, (String) null, null));
      request.selectVariant(list);
      return Response.status(Response.Status.PRECONDITION_FAILED).build();
   }
}
