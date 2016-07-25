package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

@Path("/products")
public class JacksonJaxbCoexistenceJacksonResource {

    @GET
    @Produces("application/json")
    @Path("{id}")
    public JacksonJaxbCoexistenceProduct getProduct() {
        return new JacksonJaxbCoexistenceProduct(333, "Iphone");
    }

    @GET
    @Produces("application/json")
    public JacksonJaxbCoexistenceProduct[] getProducts() {

        JacksonJaxbCoexistenceProduct[] products = {new JacksonJaxbCoexistenceProduct(333, "Iphone"), new JacksonJaxbCoexistenceProduct(44, "macbook")};
        return products;
    }

    @POST
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    @Path("{id}")
    public JacksonJaxbCoexistenceProduct post(JacksonJaxbCoexistenceProduct p) {
        return p;
    }

}
