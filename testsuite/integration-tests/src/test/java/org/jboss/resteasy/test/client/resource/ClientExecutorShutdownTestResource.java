package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/test")
public class ClientExecutorShutdownTestResource {

    private static final Logger logger = Logger.getLogger(ClientExecutorShutdownTestResource.class);

    @POST
    public void post() {
        logger.info("In POST");
    }
}
