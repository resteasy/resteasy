package org.jboss.resteasy.plugins.server.netty.cdi;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

/**
 * Created by John.Ament on 2/23/14.
 */
@RequestScoped
@Path("/echo")
public class EchoResource {

    @GET
    @Produces("text/plain")
    public String greet(@QueryParam("name") final String name) {
        if (name.equals("null")) {
            throw new NullPointerException("you sent null");
        }
        return String.format("Hello, %s!", name);
    }
}
