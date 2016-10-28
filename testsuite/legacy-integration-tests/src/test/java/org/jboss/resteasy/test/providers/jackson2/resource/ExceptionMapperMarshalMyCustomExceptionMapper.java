package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
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
