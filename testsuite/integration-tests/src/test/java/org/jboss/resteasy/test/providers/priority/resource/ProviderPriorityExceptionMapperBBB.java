package org.jboss.resteasy.test.providers.priority.resource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(20)
public class ProviderPriorityExceptionMapperBBB implements ExceptionMapper<ProviderPriorityTestException> {

    @Override
    public Response toResponse(ProviderPriorityTestException exception) {
        return Response.ok().status(444).entity("BBB").build();
    }
}
