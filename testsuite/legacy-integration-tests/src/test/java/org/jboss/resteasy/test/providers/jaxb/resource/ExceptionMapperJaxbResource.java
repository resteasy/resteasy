package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/test")
public class ExceptionMapperJaxbResource {
    @POST
    @Consumes("application/xml")
    public void post(AbstractJaxbClassPerson person) {
    }
}
