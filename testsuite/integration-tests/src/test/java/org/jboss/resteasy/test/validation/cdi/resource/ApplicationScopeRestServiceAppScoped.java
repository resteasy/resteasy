package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationScopeRestServiceAppScoped implements ApplicationScopeIRestServiceAppScoped {

    private static final Logger logger = Logger.getLogger(ApplicationScopeRestServiceAppScoped.class);

    public String sendDto(ApplicationScopeMyDto myDto) {
        if (logger.isDebugEnabled()) {
            logger.debug("RestServiceAppScoped: Nevertheless: " + myDto, new Exception("RestServiceAppScoped"));
        }
        return myDto == null ? null : myDto.getPath();
    }
}
