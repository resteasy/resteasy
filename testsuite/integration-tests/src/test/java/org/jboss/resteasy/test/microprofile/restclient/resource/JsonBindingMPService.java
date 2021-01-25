package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
