package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;

@RequestScoped
public class ApplicationScopeRestServiceReqScoped implements ApplicationScopeIRestServiceReqScoped {
   
    public Response sendDto(ApplicationScopeMyDto myDto) {
        System.out.println("RestServiceReqScoped: Nevertheless: " + myDto);
        new Exception("RestServiceReqScoped").printStackTrace();
        return Response.ok(myDto == null ? "null" : myDto.getPath()).header("entered", "true").build();
    }
}
