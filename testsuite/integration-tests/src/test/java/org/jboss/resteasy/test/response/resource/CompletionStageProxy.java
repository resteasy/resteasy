package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.concurrent.CompletionStage;


@Path("")
public interface CompletionStageProxy {
   @GET
   @Path("sleep")
   @Produces("text/plain")
   CompletionStage<String> sleep();
}
