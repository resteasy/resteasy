package org.jboss.resteasy.test.nextgen.wadl.resources;

import javax.ws.rs.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/basic")
public class BasicResource {

    private String name;

    @GET
    public String hello(@PathParam("name") String name) {
        return "Hello, " + name;
    }

    @POST
    public void input(@PathParam("name2") String name2) {
        this.name = name2;
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
