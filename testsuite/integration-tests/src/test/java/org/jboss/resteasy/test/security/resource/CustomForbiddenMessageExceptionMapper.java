package org.jboss.resteasy.test.security.resource;


import org.jboss.logging.Logger;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CustomForbiddenMessageExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private static Logger log = Logger.getLogger(CustomForbiddenMessageExceptionMapper.class);

    @Override
    public Response toResponse(ForbiddenException e) {
        log.info("Entering exception mapper");
        String entity = (String) e.getResponse().getEntity();
        return Response.status(403).header("Content-Type", "text/plain").entity("My custom message from CustomForbiddenMessageExceptionMapper: " + entity).build();
    }
}
