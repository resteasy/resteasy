package org.jboss.resteasy.test.client.resource;

import java.io.InputStream;

import jakarta.activation.DataSource;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.logging.Logger;

@Path("/")
public class EntityBufferingInFileResource {

    private static final Logger logger = Logger.getLogger(EntityBufferingInFileResource.class);

    @POST
    @Produces("text/plain")
    @Path("hello")
    public InputStream resourceMethod(DataSource ds) throws Exception {
        logger.info("entered resourceMethod()");
        return ds.getInputStream();
    }
}
