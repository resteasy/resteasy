package org.jboss.resteasy.test.response.resource;

import static jakarta.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Variant;

import org.jboss.resteasy.test.response.VariantAcceptTest;

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
        List<Variant> variants = Variant
                .mediaTypes(VariantAcceptTest.TEXT_PLAIN_WITH_PARAMS, VariantAcceptTest.TEXT_HTML_WITH_PARAMS).build();
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
