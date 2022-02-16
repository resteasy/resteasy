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
      NewCookie cookie = new NewCookie.Builder("meaning")
              .value("42")
              .path(null)
              .domain(null)
              .version(NewCookie.DEFAULT_VERSION)
              .comment(null)
              .maxAge(NewCookie.DEFAULT_MAX_AGE)
              .expiry(null)
              .secure(false)
              .httpOnly(true)
              .build();
      return Response.ok().cookie(cookie).build();
   }

   @GET
   @Path("default")
   public Response getDefault() {
      NewCookie cookie = new NewCookie.Builder("meaning")
              .value("42")
              .build();
      return Response.ok().cookie(cookie).build();
   }
}
