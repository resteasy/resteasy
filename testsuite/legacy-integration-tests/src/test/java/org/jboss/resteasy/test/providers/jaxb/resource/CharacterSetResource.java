package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.util.List;

@Path("/")
public class CharacterSetResource {

    private final String[] characterSets = {"US-ASCII", "UTF-8", "ISO-8859-1"};

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
