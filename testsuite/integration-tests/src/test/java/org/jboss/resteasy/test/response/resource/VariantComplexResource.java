package org.jboss.resteasy.test.response.resource;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

@Path("/complex")
public class VariantComplexResource {
    @GET
    public Response doGet(@Context Request req) {
        List<Variant> vs = Variant.VariantListBuilder.newInstance().mediaTypes(MediaType.valueOf("image/jpeg")).add()
                .mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().mediaTypes(
                        MediaType.valueOf("text/xml"))
                .languages(new Locale("en")).add().mediaTypes(
                        MediaType.valueOf("text/xml"))
                .languages(new Locale("en", "us")).add().build();

        Variant v = req.selectVariant(vs);
        if (v == null) {
            return Response.notAcceptable(vs).build();
        } else {
            return Response.ok("GET", v).build();
        }
    }
}
