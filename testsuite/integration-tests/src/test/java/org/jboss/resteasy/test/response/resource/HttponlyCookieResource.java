package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("cookie")
public class HttponlyCookieResource {

   @GET
   @Path("true")
   public Response getTrue() {
      NewCookie cookie = new NewCookie("meaning", "42", null, null, NewCookie.DEFAULT_VERSION, null, NewCookie.DEFAULT_MAX_AGE, null, false, true);
      return Response.ok().cookie(cookie).build();
   }

   @GET
   @Path("default")
   public Response getDefault() {
      NewCookie cookie = new NewCookie("meaning", "42");
      return Response.ok().cookie(cookie).build();
   }
}
