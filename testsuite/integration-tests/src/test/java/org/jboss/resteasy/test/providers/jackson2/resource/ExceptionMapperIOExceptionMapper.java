package org.jboss.resteasy.test.providers.jackson2.resource;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * User: rsearls
 * Date: 10/26/16
 */
@Provider
public class ExceptionMapperIOExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    @Override
    public Response toResponse(JsonProcessingException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("err_msg", "UN_KNOWN_ERR");
        result.put("err_detail", "please contact admin for help");
        return Response.status(Response.Status.OK).entity(result)
                .type(MediaType.APPLICATION_JSON_TYPE).build();

    }
}
