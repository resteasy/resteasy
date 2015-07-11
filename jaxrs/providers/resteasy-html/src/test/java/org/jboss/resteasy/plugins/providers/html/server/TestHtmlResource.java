package org.jboss.resteasy.plugins.providers.html.server;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.html.Redirect;
import org.jboss.resteasy.plugins.providers.html.View;
import org.jboss.resteasy.plugins.providers.html.Viewable;

@Path("/hello")
public class TestHtmlResource
{

   @GET
   @Produces(MediaType.TEXT_HTML)
   @Path("/{param}")
   public Redirect testParam(@PathParam("param") @Encoded String value)
   {
      String path = "/test/hello?value=" + value;
      System.out.println("redireting to " + path);
      return new Redirect(path);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public View testTextView(@QueryParam("value") @DefaultValue("world") String value)
   {
      return new View("/WEB-INF/hello_text.jsp", value, "helloTo");
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public View testHtmlView(@QueryParam("value") @DefaultValue("world") String value)
   {
      return new View("/WEB-INF/hello_html.jsp", value, "helloTo");
   }

   @GET
   @Produces(MediaType.APPLICATION_XHTML_XML)
   public View testXHtmlView(@QueryParam("value") @DefaultValue("world") String value)
   {
      return new View("/WEB-INF/hello_xhtml.jsp", value, "helloTo");
   }

   @GET
   @Produces(MediaType.APPLICATION_XHTML_XML)
   @Path("/viewable")
   public Response testXHtmlViewable(@QueryParam("value") @DefaultValue("world") String value)
   {
      return Response
         .ok(new Viewable("/WEB-INF/hello_xhtml.jsp", value, "helloTo"))
         .header("X-KEY", "X-VALUE")
         .build();
   }
}
