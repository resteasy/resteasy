package org.jboss.resteasy.test.microprofile.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public interface HelloClient {

    @GET
    @Path("/hello")
    String hello();

}