package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Locale;

@Path("/complex")
public class VariantComplexResource {
   @GET
   public Response doGet(@Context Request req) {
      List<Variant> vs = Variant.VariantListBuilder.newInstance().mediaTypes(MediaType.valueOf("image/jpeg")).add()
            .mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().mediaTypes(
                  MediaType.valueOf("text/xml")).languages(new Locale("en")).add().mediaTypes(
                  MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().build();

      Variant v = req.selectVariant(vs);
      if (v == null) {
         return Response.notAcceptable(vs).build();
      } else {
         return Response.ok("GET", v).build();
      }
   }
}
