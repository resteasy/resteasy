package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;

@Path("/")
public class CharacterSetResource {

    private final String[] characterSets = { "US-ASCII", "UTF-8", "ISO-8859-1" };

    @GET
    @Path("variant-selection")
    @Produces("application/xml")
    public Response getVariantSelection(@Context Request request) {
        int i = characterSets.length;
        MediaType[] mediaTypes = new MediaType[i];
        while (--i >= 0) {
            mediaTypes[i] = MediaType.valueOf("application/xml;charset=" + characterSets[i]);
        }
        List<Variant> variants = Variant.mediaTypes(mediaTypes).build();
        Variant variant = request.selectVariant(variants);
        if (variant == null) {
            return Response.notAcceptable(variants).build();
        }
        return Response.ok(new CharacterSetData(), variant).build();
    }
}
