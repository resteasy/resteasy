package org.jboss.resteasy.test.providers.jackson.resource;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/xml/products")
public class JacksonXmlResource {

    @BadgerFish
    @GET
    @Produces("application/json")
    @Path("{id}")
    public JacksonXmlProduct getProduct() {
        return new JacksonXmlProduct(333, "Iphone");
    }

    @GET
    @Produces("application/json")
    @NoJackson
    public JacksonXmlProduct[] getProducts() {

        JacksonXmlProduct[] products = {new JacksonXmlProduct(333, "Iphone"), new JacksonXmlProduct(44, "macbook")};
        return products;
    }

}
