package org.jboss.resteasy.test.providers.preference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("test")
@Produces("text/plain")
public class StringResource {

    @GET
    public String get()
    {
        return "Hello world!";
    }
}
