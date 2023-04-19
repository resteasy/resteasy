package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/nowhere")
public interface ReaderWriterNowhereClient {
    @GET
    @Produces("text/plain")
    Response read();
}
