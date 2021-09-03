package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

public class RequestFilterGetEntity implements ClientRequestFilter {

   private static Logger logger = Logger.getLogger(RequestFilterGetEntity.class);

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      logger.info("*** filter 2 ***");
      Object entity = requestContext.getEntity();
      Assert.assertEquals("The requestContext doesn't contain the correct entity", "test", entity);
      MediaType mt = requestContext.getMediaType();
      Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, mt);
      Assert.assertEquals(String.class, requestContext.getEntityType());
      requestContext.abortWith(Response.ok().build());

   }
}
