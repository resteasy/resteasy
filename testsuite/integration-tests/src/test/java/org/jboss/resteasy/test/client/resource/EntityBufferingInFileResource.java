package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.activation.DataSource;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.io.InputStream;

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
