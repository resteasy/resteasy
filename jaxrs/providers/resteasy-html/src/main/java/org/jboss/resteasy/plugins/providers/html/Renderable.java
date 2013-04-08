package org.jboss.resteasy.plugins.providers.html;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * If you return one of these from a resource method, RESTEasy-HTML will render it.  This
 * interface allows for exotic view rendering types.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public interface Renderable
{
   /**
    * Called to do the actual work of rendering a view.  Note that while ServletException
    * can be thrown, WebApplicationException is preferred.
    */
   public void render(HttpServletRequest request, HttpServletResponse response)
       throws IOException, ServletException, WebApplicationException;

}
