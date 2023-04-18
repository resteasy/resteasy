package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/test")
@Produces("application/xml")
public class CustomOverrideResource {
    @GET
    public Response getFooXml() {
        CustomOverrideFoo foo = new CustomOverrideFoo();
        foo.setName("bill");
        return Response.ok(foo).build();
    }

    @GET
    @Produces("text/x-vcard")
    public Response getFooVcard() {
        CustomOverrideFoo foo = new CustomOverrideFoo();
        foo.setName("bill");
        return Response.ok(foo).build();
    }
}
