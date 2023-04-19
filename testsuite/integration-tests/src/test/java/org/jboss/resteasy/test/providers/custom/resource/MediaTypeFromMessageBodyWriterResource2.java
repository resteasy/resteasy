package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("")
public class MediaTypeFromMessageBodyWriterResource2 {

    @GET
    public Response getJson() {
        return Response.ok().entity(new CustomProviderPreferenceUser("dummy", "dummy@dummy.com")).build();
    }
}
