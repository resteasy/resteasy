package org.jboss.resteasy.plugins.server.netty.cdi;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by John.Ament on 2/23/14.
 */
@RequestScoped
@Path("/echo")
public class EchoResource {

    @GET
    @Produces("text/plain")
    public String greet(@QueryParam("name") final String name)
    {
        if(name.equals("null"))
        {
            throw new NullPointerException("you sent null");
        }
        return String.format("Hello, %s!",name);
    }
}
