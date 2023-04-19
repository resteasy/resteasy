package org.jboss.resteasy.test.client.resource;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Resource(name = "NullEntityResource")
@Path("")
public class NullEntityResource {
    @POST
    @Path("null")
    @Produces("text/plain")
    public String post(String entity) {
        return entity;
    }

    @POST
    @Path("entity")
    @Produces("text/plain")
    public String empty(String entity) {
        return entity;
    }

    @POST
    @Path("form")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public String form(@FormParam("s") String s) {
        return s;
    }

    @POST
    @Path("html")
    @Consumes("text/html")
    @Produces("text/plain")
    public String html(String html) {
        return html;
    }

    @POST
    @Path("xhtml")
    @Consumes("application/xhtml+xml")
    @Produces("text/plain")
    public String xhtml(String xhtml) {
        return xhtml;
    }

    @POST
    @Path("xml")
    @Consumes("application/xml")
    @Produces("text/plain")
    public String xml(String xml) {
        return xml;
    }
}
