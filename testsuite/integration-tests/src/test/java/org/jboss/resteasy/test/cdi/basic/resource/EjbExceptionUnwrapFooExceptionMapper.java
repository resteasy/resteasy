package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EjbExceptionUnwrapFooExceptionMapper implements ExceptionMapper<EjbExceptionUnwrapFooException> {
    public Response toResponse(EjbExceptionUnwrapFooException exception) {
        return Response.status(409).build();
    }

}
