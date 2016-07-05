package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
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
