package org.jboss.resteasy.test.nextgen.wadl.resources;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/basic")
public class BasicResource {

   private String name;

   @GET
   public String get(@PathParam("name") String name) {
      return "Hello, " + name;
   }

   @POST
   public void post(@PathParam("name2") String name2) {
      this.name = name2;
   }

   @GET
   @Path("composite/{pathParam}")
   @Produces("text/plain")
   public String composite(@PathParam("pathParam") String pathParam,
                     @HeaderParam("headerParam") String headerParam,
                     @QueryParam("queryParam") String queryParam,
                     @MatrixParam("matrixParam") String matrixParam,
                     @CookieParam("cookieParam") String cookieParam) {
      return "p:P;h:H;q:Q;m:M;c:C"
            .replaceAll("P", pathParam)
            .replaceAll("H", headerParam)
            .replaceAll("Q", queryParam)
            .replaceAll("M", matrixParam)
            .replaceAll("C", cookieParam);
   }

}
