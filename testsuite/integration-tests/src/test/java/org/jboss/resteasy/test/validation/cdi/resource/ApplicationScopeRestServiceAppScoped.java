package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationScopeRestServiceAppScoped implements ApplicationScopeIRestServiceAppScoped {

    public String sendDto(ApplicationScopeMyDto myDto) {
        System.out.println("RestServiceAppScoped: Nevertheless: " + myDto);
        new Exception("RestServiceAppScoped").printStackTrace();
        return myDto == null ? null : myDto.getPath();
    }
}
