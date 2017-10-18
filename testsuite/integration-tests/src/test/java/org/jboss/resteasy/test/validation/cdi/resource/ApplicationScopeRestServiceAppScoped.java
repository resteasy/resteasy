package org.jboss.resteasy.test.validation.cdi.resource;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationScopeRestServiceAppScoped implements ApplicationScopeIRestServiceAppScoped {

    private final static Logger logger = Logger.getLogger(ApplicationScopeRestServiceAppScoped.class);

    public String sendDto(ApplicationScopeMyDto myDto) {
        if (logger.isDebugEnabled())
        {
            logger.debug("RestServiceAppScoped: Nevertheless: " + myDto, new Exception("RestServiceAppScoped"));
        }
        return myDto == null ? null : myDto.getPath();
    }
}
