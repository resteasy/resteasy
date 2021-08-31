package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("cookie")
public class CookieResource {
   @GET
   @Path("weird")
   @Produces("text/plain")
   public Response responseOkWeird() {
      return Response.ok("ok")
            .header("Set-Cookie",
                  "guid=1.9112608617070927872;Path=/;Domain=localhost;Expires=Thu, 03-May-2028 10:36:34 GMT;Max-Age=150000000")
            .build();
   }

   @GET
   @Path("standard")
   @Produces("text/plain")
   public Response responseOkStandard() {
      return Response.ok("ok")
            .header("Set-Cookie", "UserID=JohnDoe; Max-Age=3600; Version=1")
            .build();
   }

}
