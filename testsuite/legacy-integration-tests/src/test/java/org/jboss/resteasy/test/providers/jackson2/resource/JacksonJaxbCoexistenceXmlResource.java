package org.jboss.resteasy.test.providers.jackson2.resource;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/xml/products")
public class JacksonJaxbCoexistenceXmlResource {

    @GET
    @Produces("application/json")
    @Path("{id}")
    @BadgerFish
    public JacksonJaxbCoexistenceXmlProduct getProduct() {
        return new JacksonJaxbCoexistenceXmlProduct(333, "Iphone");
    }

    @GET
    @Produces("application/json")
    @NoJackson
    public JacksonJaxbCoexistenceXmlProduct[] getProducts() {

        JacksonJaxbCoexistenceXmlProduct[] products = {new JacksonJaxbCoexistenceXmlProduct(333, "Iphone"), new JacksonJaxbCoexistenceXmlProduct(44, "macbook")};
        return products;
    }

}
