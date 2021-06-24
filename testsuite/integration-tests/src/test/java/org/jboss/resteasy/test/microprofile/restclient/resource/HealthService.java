package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.Closeable;

public interface HealthService extends Closeable {
    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/health")
    HealthCheckData getHealthData()         throws WebApplicationException;
}