package org.jboss.resteasy.test.response.resource;

import org.jboss.resteasy.test.response.VariantAcceptTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Variant;
import java.util.List;

import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

@Path("")
public class VariantAcceptResource {
    @Context
    private Request request;

    @Context
    private HttpHeaders headers;

    @GET
    @Path("variant")
    public String variant() {
        List<Variant> variants = Variant.mediaTypes(TEXT_PLAIN_TYPE, TEXT_HTML_TYPE).build();
        MediaType selected = request.selectVariant(variants).getMediaType();
        return selected.toString();
    }

    @GET
    @Path("params")
    public String params() {
        List<Variant> variants = Variant.mediaTypes(VariantAcceptTest.TEXT_PLAIN_WITH_PARAMS, VariantAcceptTest.TEXT_HTML_WITH_PARAMS).build();
        MediaType selected = request.selectVariant(variants).getMediaType();
        return selected.toString();
    }

    @GET
    @Path("simple")
    public String simple() {
        return "Hello";
    }

    @GET
    @Path("simpleqs")
    @Produces("application/json;qs=0.5")
    public String simpleqs1() {
        return "Hello";
    }

    @GET
    @Path("simpleqs")
    @Produces("application/xml;qs=0.9")
    public String simpleqs2() {
        return "Hello";
    }
}
