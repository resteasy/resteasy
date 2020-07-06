package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
