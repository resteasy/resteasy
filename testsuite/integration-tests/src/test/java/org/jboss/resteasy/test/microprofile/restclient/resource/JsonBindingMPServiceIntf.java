package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;

import javax.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@RegisterRestClient(baseUri ="http://localhost:8080/jsonBinding_service")
@Path("/jsonBindingService")
@Singleton
public interface JsonBindingMPServiceIntf {

    @Path("/dog")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Dog getDog(Dog dog) throws Exception;
}
