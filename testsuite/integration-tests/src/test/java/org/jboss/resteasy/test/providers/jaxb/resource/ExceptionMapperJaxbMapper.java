package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;

@Provider
public class ExceptionMapperJaxbMapper implements ExceptionMapper<JAXBUnmarshalException> {
    @Override
    public Response toResponse(JAXBUnmarshalException exception) {
        return Response.status(400).type("text/plain").entity(exception.getMessage()).build();
    }
}
