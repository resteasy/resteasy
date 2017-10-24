package org.jboss.resteasy.test.validation.cdi.resource;

import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;

@RequestScoped
public class ApplicationScopeRestServiceReqScoped implements ApplicationScopeIRestServiceReqScoped {

    private final static Logger logger = Logger.getLogger(ApplicationScopeRestServiceReqScoped.class);

    public Response sendDto(ApplicationScopeMyDto myDto) {
        if (logger.isDebugEnabled())
        {
            logger.debug("RestServiceReqScoped: Nevertheless: " + myDto, new Exception("RestServiceReqScoped"));
        }
        return Response.ok(myDto == null ? "null" : myDto.getPath()).header("entered", "true").build();
    }
}
