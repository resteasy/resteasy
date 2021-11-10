package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/")
public class AbortMessageResourceFilter implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      requestContext.abortWith(Response.ok("aborted").header("Aborted", "true").type(MediaType.TEXT_PLAIN_TYPE).build());
   }

   @Path("/showproblem")
   @GET
   @Produces({MediaType.TEXT_PLAIN})
   public Response showProblem() {
      Client c = ClientBuilder.newClient().register(this);
      try {
         return c.target("http://doesnotmattersinceitisaborted").request().get();
      } finally {
         c.close();
      }
   }
}
