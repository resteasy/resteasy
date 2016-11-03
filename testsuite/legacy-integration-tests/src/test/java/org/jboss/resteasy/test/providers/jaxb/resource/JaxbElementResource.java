package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;;

@Path("resource")
public class JaxbElementResource {

    @POST
    @Path("standardwriter")
    public String bytearraywriter(String value) {
        return value;
    }
}
