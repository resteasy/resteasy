package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class TypeMappingResource {

    @GET
    @Path("/noproduces")
    public TypeMappingBean get() {
        return new TypeMappingBean("name");
    }
}
