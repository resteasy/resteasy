package org.jboss.resteasy.test.client.resource;

import org.junit.Assert;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class ClientResponseFilterLength implements ClientResponseFilter {
   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      Assert.assertEquals("The length of the response is not the expected one", 10, responseContext.getLength());
   }
}
