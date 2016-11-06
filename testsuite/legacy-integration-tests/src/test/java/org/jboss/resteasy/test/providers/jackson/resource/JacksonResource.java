package org.jboss.resteasy.test.providers.jackson.resource;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;

@Path("/products")
public class JacksonResource {

    @GET
    @Produces("application/json")
    @Path("{id}")
    public JacksonProduct getProduct() {
        return new JacksonProduct(333, "Iphone");
    }

    @GET
    @Produces("application/json")
    public JacksonProduct[] getProducts() {

        JacksonProduct[] products = {new JacksonProduct(333, "Iphone"), new JacksonProduct(44, "macbook")};
        return products;
    }

    @POST
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    @Path("{id}")
    public JacksonProduct post(JacksonProduct p) {
        return p;
    }

}
