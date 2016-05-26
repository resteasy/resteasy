package org.jboss.resteasy.plugins.providers.html;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/hello")
public class TestHtmlResource
{
   @GET
   @Produces("text/html")
   public View testHtml(@QueryParam("value") @DefaultValue("world") String value)
   {
      return new View("/WEB-INF/hello.jsp", value, "helloTo");
   }

   @GET
   @Produces("text/html")
   @Path("/{param}")
   public Redirect testParam(@PathParam("param") @Encoded String value)
   {
      String path = "/hello?value=" + value;
      System.out.println("redireting to " + path);
      return new Redirect(path);
   }
}
