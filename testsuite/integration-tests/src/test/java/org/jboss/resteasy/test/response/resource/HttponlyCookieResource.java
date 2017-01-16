package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

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
