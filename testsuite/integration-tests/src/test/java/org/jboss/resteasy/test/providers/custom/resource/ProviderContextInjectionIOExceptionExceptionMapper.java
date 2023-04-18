package org.jboss.resteasy.test.providers.custom.resource;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProviderContextInjectionIOExceptionExceptionMapper implements ExceptionMapper<IOException> {

    @Override
    public Response toResponse(IOException exception) {
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
