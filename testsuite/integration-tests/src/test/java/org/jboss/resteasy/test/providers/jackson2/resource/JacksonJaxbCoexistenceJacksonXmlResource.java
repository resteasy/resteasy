package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/jxml/products")
public class JacksonJaxbCoexistenceJacksonXmlResource {

    @GET
    @Produces("application/json")
    @Path("{id}")
    public JacksonJaxbCoexistenceProduct2 getProduct() {
        return new JacksonJaxbCoexistenceProduct2(333, "Iphone");
    }

    @GET
    @Produces("application/json")
    public JacksonJaxbCoexistenceProduct2[] getProducts() {

        JacksonJaxbCoexistenceProduct2[] products = {new JacksonJaxbCoexistenceProduct2(333, "Iphone"), new JacksonJaxbCoexistenceProduct2(44, "macbook")};
        return products;
    }

}
