package org.jboss.resteasy.plugins.server.netty.cdi;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by John.Ament on 2/23/14.
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception>{
    @Override
    public Response toResponse(Exception exception) {
        return Response.status(406).entity(exception.getMessage()).build();
    }
}
