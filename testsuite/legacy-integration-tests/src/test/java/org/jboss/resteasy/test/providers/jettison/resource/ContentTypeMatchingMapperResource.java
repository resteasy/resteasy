package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/mapper")
public class ContentTypeMatchingMapperResource {
    @Path("produces")
    @Produces("application/xml")
    @GET
    public String getProduces() {
        throw new ContentTypeMatchingErrorException();
    }

    @Path("accepts-produces")
    @Produces({"application/xml", "application/json"})
    @GET
    public String getAcceptsProduces() {
        throw new ContentTypeMatchingErrorException();
    }

    @Path("accepts")
    @GET
    public String getAccepts() {
        throw new ContentTypeMatchingErrorException();
    }

    @Path("accepts-entity")
    @GET
    public ContentTypeMatchingError getEntity() {
        return new ContentTypeMatchingError();
    }
}
