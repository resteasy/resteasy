package org.jboss.resteasy.test.cdi.ejb.resource;

import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Singleton
@Remote
@Path("greeter")
public class EJBCDIValidationEJBProxyGreeterResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String greet(@Valid EJBCDIValidationEJBProxyGreeting greeting) {
        return "Hello " + greeting.getName() + "!";
    }
}
