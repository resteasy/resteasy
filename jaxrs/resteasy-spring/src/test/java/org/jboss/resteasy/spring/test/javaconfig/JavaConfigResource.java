package org.jboss.resteasy.spring.test.javaconfig;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This class provides a web-based facade for an injected service.
 */
@Path("")
public class JavaConfigResource {
    JavaConfigService service;
    @Autowired
    public void setService(JavaConfigService service) {
        System.out.println("*** service injected=" + service);
        this.service = service;
    }    
    
    public JavaConfigResource() {
        System.out.println("*** resource created:" + super.toString());
    }
    
    
    @GET
    @Path("invoke")
    @Produces(MediaType.TEXT_PLAIN)
    public String invoke() {
        return service.invoke();
    }
}
