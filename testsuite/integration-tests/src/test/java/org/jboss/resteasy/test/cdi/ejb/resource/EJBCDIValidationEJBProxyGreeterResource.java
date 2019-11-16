package org.jboss.resteasy.test.cdi.ejb.resource;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
