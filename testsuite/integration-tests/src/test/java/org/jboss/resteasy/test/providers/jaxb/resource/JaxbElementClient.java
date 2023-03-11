package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.xml.bind.JAXBElement;

@Consumes("application/xml")
@Produces("application/xml")
public interface JaxbElementClient {

    @GET
    @Path("/{name}")
    JAXBElement<Parent> getParent(@PathParam("name") String name);

    @POST
    JAXBElement<Parent> postParent(JAXBElement<Parent> parent);

}
