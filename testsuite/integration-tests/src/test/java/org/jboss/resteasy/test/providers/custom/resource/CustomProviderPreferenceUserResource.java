package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/user")
@Produces("text/plain")
public class CustomProviderPreferenceUserResource {

   private static final CustomProviderPreferenceUser user = new CustomProviderPreferenceUser("jharting", "email@example.com");

   @GET
   public CustomProviderPreferenceUser getUser() {
      return user;
   }
}
