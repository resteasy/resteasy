package org.jboss.resteasy.test.providers.jackson2.resource;


import org.jboss.resteasy.spi.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
