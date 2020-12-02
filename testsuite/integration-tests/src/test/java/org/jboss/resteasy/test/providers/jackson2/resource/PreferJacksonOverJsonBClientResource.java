package org.jboss.resteasy.test.providers.jackson2.resource;


import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;

@Path("/")
public class PreferJacksonOverJsonBClientResource {

   @GET
   @Path("core")
   @Produces(MediaType.APPLICATION_JSON)
   public Date core() {
      return new Date();
   }

   @GET
   @Path("call")
   @Produces("text/plain")
   public String call(@HeaderParam("clientURL") String clientURL) throws Exception {

      Client client = ClientBuilder.newClient();
      try {
         WebTarget target = client.target(clientURL);
         Response response = target.request().get();

         if (response.getStatus() != HttpResponseCodes.SC_OK) {
            throw new Exception("Client in deployment received wrong response code");
         }

         String responseText = response.readEntity(String.class);
         return responseText;
      } finally {
         client.close();
      }
   }
}
