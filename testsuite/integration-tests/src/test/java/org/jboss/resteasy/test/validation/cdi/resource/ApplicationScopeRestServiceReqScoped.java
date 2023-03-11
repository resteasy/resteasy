package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@RequestScoped
public class ApplicationScopeRestServiceReqScoped implements ApplicationScopeIRestServiceReqScoped {

    private static final Logger logger = Logger.getLogger(ApplicationScopeRestServiceReqScoped.class);

    public Response sendDto(ApplicationScopeMyDto myDto) {
        if (logger.isDebugEnabled()) {
            logger.debug("RestServiceReqScoped: Nevertheless: " + myDto, new Exception("RestServiceReqScoped"));
        }
        return Response.ok(myDto == null ? "null" : myDto.getPath()).header("entered", "true").build();
    }
}
