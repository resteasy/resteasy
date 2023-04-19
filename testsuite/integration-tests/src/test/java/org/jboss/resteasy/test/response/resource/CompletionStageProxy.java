package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public interface CompletionStageProxy {
    @GET
    @Path("sleep")
    @Produces("text/plain")
    CompletionStage<String> sleep();
}
