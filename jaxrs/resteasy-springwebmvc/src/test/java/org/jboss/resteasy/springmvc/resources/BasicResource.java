package org.jboss.resteasy.springmvc.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/basic")
public class BasicResource {

    @GET
    @Produces("text/plain")
    public String getBasic(){
        return "test";
    }
}
