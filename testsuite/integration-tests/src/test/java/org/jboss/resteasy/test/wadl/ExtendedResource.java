package org.jboss.resteasy.test.wadl;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/extended")
public class ExtendedResource {

    @POST
    @Consumes({ "application/xml" })
    public String post(ListType income) {
        return "foo";
    }
}
