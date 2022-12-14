package org.jboss.resteasy.test.mapper.resource;

import java.io.IOException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

    // This mapper forces a 204 status code by ExceptionHandler
    public Response toResponse(IOException e) {
        return null;
    }
}
