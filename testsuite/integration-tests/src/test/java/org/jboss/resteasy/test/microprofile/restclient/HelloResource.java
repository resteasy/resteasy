package org.jboss.resteasy.test.microprofile.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class HelloResource {

    @GET
    @Produces("text/plain")
    @Path("/hello")
    public String hello() {
       return "Hello";
    }

}