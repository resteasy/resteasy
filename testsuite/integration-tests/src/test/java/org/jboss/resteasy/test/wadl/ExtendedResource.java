package org.jboss.resteasy.test.wadl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/extended")
public class ExtendedResource {

    @POST
    @Consumes({"application/xml"})
    public String post(ListType income) {
        return "foo";
    }
}

