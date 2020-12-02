package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/jsonBindingService")
public class JsonBindingMPService {

    @Path("/dog")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Dog getDog(Dog dog) throws Exception {
        dog.setNameAndSort("Jethro", "stafford");
        return dog;
    }
}
