package org.jboss.resteasy.test.exception.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Provider
@ApplicationScoped
@FollowUpRequired("The @ApplicationScope annotation can be removed once @Provider is a bean defining annotation.")
public class ExceptionMapperInjectionNotFoundMapper implements
        ExceptionMapper<NotFoundException> {
    private static Logger logger = Logger.getLogger(ExceptionMapperInjectionNotFoundMapper.class);

    @Inject
    HttpHeaders httpHeaders;

    public Response toResponse(NotFoundException exception) {
        logger.info(String.format("Request headers: %s", httpHeaders.getRequestHeaders()));
        logger.info("Exception is mapped!");
        return Response.status(HttpResponseCodes.SC_HTTP_VERSION_NOT_SUPPORTED).build();
    }
}
