package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;

@Consumes("application/json")
@Produces("application/json")
public interface JaxbJsonElementClient {

    @GET
    @Path("/{name}")
    JAXBElement<Parent> getParent(@PathParam("name") String name);

    @POST
    JAXBElement<Parent> postParent(JAXBElement<Parent> parent);

}
