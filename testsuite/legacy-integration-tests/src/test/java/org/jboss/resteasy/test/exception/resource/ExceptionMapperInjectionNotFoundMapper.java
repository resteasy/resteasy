package org.jboss.resteasy.test.exception.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperInjectionNotFoundMapper implements
        ExceptionMapper<NotFoundException> {
    private static Logger logger = Logger.getLogger(ExceptionMapperInjectionNotFoundMapper.class);

    @Context
    HttpHeaders httpHeaders;

    public Response toResponse(NotFoundException exception) {
        logger.info(String.format("Request headers: %s", httpHeaders.getRequestHeaders()));
        logger.info("Exception is mapped!");
        return Response.status(HttpResponseCodes.SC_HTTP_VERSION_NOT_SUPPORTED).build();
    }
}
