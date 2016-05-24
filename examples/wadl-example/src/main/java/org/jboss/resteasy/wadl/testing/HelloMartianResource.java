package org.jboss.resteasy.wadl.testing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/martian")
public class HelloMartianResource {

    private String name;

    @GET
    public String hello() {
        return "Hello, Martian!";
    }

    @POST
    public void input(@PathParam("name") String name) {
        this.name = name;
    }


    @GET
    @Path("ab/{a}")
    @Produces("text/plain")
    public String abc(@PathParam("a") String a, @CookieParam("b") int b) {
        return a + b;
    }

    @GET
    @Path("intr/{foo}")
    @Produces("text/plain")
    public int integerReturn(@PathParam("foo") int foo) {
        return foo;
    }


}
