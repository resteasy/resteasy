package org.jboss.resteasy.test.providers.jackson2.resource;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * User: rsearls
 * Date: 10/26/16
 */
@Provider
public class ExceptionMapperIOExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable e) {
        Map<String, Object> result = new HashMap<>();
        result.put("err_msg", "UN_KNOWN_ERR");
        result.put("err_detail", "please contact admin for help");
        return Response.status(Response.Status.OK).entity(result)
            .type(MediaType.APPLICATION_JSON_TYPE).build();

    }
}