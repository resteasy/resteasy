package org.jboss.resteasy.test.core.logging.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class DebugLoggingEndPoint {

    @POST
    @Path("custom")
    @Produces("aaa/bbb")
    @Consumes("aaa/bbb")
    public String custom(String data) {
        return data;
    }

    @POST
    @Path("build/in")
    public String buildIn(String data) {
        return data;
    }
}
