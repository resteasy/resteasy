package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class ExceptionMapperMarshalMyCustomExceptionMapper implements ExceptionMapper<ExceptionMapperMarshalMyCustomException> {
   @Override
   public Response toResponse(ExceptionMapperMarshalMyCustomException exception) {
      List<ExceptionMapperMarshalErrorMessage> list = new ArrayList<ExceptionMapperMarshalErrorMessage>();
      list.add(new ExceptionMapperMarshalErrorMessage("error"));
      return Response.ok(list, MediaType.APPLICATION_JSON_TYPE).build();
   }
}
