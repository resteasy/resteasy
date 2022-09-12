package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import java.util.List;

@Path("/encoding")
public class VariantEncodingResource {
   @GET
   public Response doGet(@Context Request req) {
      List<Variant> vs = Variant.VariantListBuilder.newInstance().encodings("enc1", "enc2", "enc3").add().build();
      Variant v = req.selectVariant(vs);
      if (v == null) {
         return Response.notAcceptable(vs).build();
      } else {
         return Response.ok(v.getEncoding(), v).build();
      }
   }
}
