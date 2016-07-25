package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

@Path("weight")
public class ResourceMatchingWeightResource {

    @POST
    @Produces("text/plain;qs=0.9")
    public String plain() {
        return MediaType.TEXT_PLAIN;
    }

    @POST
    @Produces("text/html;qs=0.8")
    public String html(@Context Request req) {
        return MediaType.TEXT_HTML;
    }

    @POST
    @Produces("text/xml;qs=0.5")
    public String xml() {
        return MediaType.TEXT_XML;
    }

    @POST
    @Produces("application/*;qs=0.5")
    public String app() {
        return MediaType.WILDCARD;
    }

    @POST
    @Produces("application/xml;qs=0.5")
    public String appxml() {
        return MediaType.APPLICATION_XML;
    }


    @POST
    @Produces("image/png;qs=0.6")
    public String png() {
        return "image/png";
    }

    @POST
    @Produces("image/*;qs=0.7")
    public String image() {
        return "image/any";
    }

    @POST
    @Produces("*/*;qs=0.1")
    public String any() {
        return MediaType.WILDCARD;
    }

}
