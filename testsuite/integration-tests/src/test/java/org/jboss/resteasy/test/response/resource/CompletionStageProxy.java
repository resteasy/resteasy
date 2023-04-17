package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("")
public interface CompletionStageProxy {
    @GET
    @Path("sleep")
    @Produces("text/plain")
    CompletionStage<String> sleep();
}
