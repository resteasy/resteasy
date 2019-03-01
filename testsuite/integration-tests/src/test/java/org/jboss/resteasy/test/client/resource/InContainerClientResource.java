package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/test-client")
public class InContainerClientResource {

   @Context
   private UriInfo uriInfo;

   @POST
   @Consumes("text/plain")
   public String post(String str) throws Exception {
      Client client = ClientBuilder.newClient();
      String result = null;
      try {
         result = client.target(uriInfo.getBaseUri() + "test").request().post(Entity.text(str)).readEntity(String.class);
      } finally {
         client.close();
      }
      return "client-post " + result;
   }
}
