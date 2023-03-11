package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Consumes("application/xml")
@Produces("application/xml")
public interface JaxbXmlRootElementClient {

    @GET
    @Path("/{name}")
    Parent getParent(@PathParam("name") String name);

    @POST
    Parent postParent(Parent parent);

}
