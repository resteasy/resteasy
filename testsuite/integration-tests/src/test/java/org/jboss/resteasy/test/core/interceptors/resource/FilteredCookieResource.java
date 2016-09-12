package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("test")
public class FilteredCookieResource {
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";
   private @Context HttpHeaders headers;
   
   @GET
   @Path("get")
   public Response getCookie() {
      NewCookie cookie = new NewCookie(OLD_COOKIE_NAME, "value");
      return Response.ok().cookie(cookie).build();
   }
   
   @GET
   @Path("return")
   public Response returnCookie() {
      Cookie oldCookie = headers.getCookies().get(OLD_COOKIE_NAME);
      Cookie newCookie = headers.getCookies().get(NEW_COOKIE_NAME);
      ResponseBuilder builder = Response.ok();
      builder.cookie(new NewCookie(oldCookie.getName(), oldCookie.getValue()));
      builder.cookie(new NewCookie(newCookie.getName(), newCookie.getValue()));
      return builder.build();
   }
}
