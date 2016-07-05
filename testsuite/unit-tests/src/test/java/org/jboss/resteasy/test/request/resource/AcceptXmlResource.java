package org.jboss.resteasy.test.request.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/xml")
public class AcceptXmlResource {
    @Consumes("application/xml;schema=foo")
    @PUT
    public void putFoo(String foo) {
    }

    @Consumes("application/xml")
    @PUT
    public void put(String foo) {
    }

    @Consumes("application/xml;schema=bar")
    @PUT
    public void putBar(String foo) {
    }


}
