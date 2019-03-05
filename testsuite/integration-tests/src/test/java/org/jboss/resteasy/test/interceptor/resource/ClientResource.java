package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class ClientResource
{
   @Context
   private UriInfo uriInfo;

   @GET
   @Path("testIt")
   public Response get()
   {
      // we need to create new client to verify that @Provider works
      Client client = ClientBuilder.newClient();
      try {
         WebTarget base = client.target(uriInfo.getBaseUriBuilder().path("clientInvoke").build());
         Response response = base.request().get();

         // return the client invocation response to make the verification in test class
         return response;
      } finally {
         client.close();
      }
   }

   @GET
   @Path("clientInvoke")
   public Response clientInvoke() {
      return Response.ok().build();
   }
}
