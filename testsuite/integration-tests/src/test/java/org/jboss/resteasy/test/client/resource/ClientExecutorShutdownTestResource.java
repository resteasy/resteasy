package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/test")
public class ClientExecutorShutdownTestResource {

    private static final Logger logger = Logger.getLogger(ClientExecutorShutdownTestResource.class);

    @POST
    public void post() {
        logger.info("In POST");
    }
}
