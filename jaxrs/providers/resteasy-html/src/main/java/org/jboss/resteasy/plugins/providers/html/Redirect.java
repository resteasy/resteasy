package org.jboss.resteasy.plugins.providers.html;

import org.jboss.resteasy.util.HttpHeaderNames;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Redirect implements Renderable
{

   private URI path;

   public Redirect(URI path)
   {
      this.path = path;
   }

   /**
    * @param path
    *           must be a valid URI
    */
   public Redirect(String path)
   {
      try
      {
         this.path = new URI(path);
      }
      catch (URISyntaxException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public URI getPath()
   {
      return this.path;
   }

   public void render(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException,
         WebApplicationException
   {
      response.setStatus(Status.SEE_OTHER.getStatusCode());
      response.setHeader(HttpHeaderNames.LOCATION, this.path.toString());
   }

}
