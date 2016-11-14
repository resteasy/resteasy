package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Path("/")
public class JacksonDatatypeEndPoint {

    @GET
    @Path("/string")
    @Produces(MediaType.APPLICATION_JSON)
    public String getString() {
        return "someString";
    }

    @GET
    @Path("/date")
    @Produces(MediaType.APPLICATION_JSON)
    public Date getDate() {
        return new Date();
    }

    @GET
    @Path("/duration")
    @Produces(MediaType.APPLICATION_JSON)
    public Duration getDuration() {
        return Duration.ofSeconds(5, 6);
    }

    @GET
    @Path("/optional/{nullParam}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<String> getOptional(@PathParam("nullParam") boolean nullParameter) {
        return nullParameter ? Optional.<String>empty() : Optional.of("info@example.com");
    }
}