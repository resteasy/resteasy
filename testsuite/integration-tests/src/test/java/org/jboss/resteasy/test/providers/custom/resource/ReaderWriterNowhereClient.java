package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/nowhere")
public interface ReaderWriterNowhereClient {
   @GET
   @Produces("text/plain")
   Response read();
}
