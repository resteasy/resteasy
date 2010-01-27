package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.servlet.http.HttpEvent;
import org.jboss.servlet.http.HttpEventServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JBossWebDispatcherServlet extends HttpServletDispatcher implements HttpEventServlet
{
   // Hack to avoid code changes within HttpServletDispatcher
   private static ThreadLocal<HttpEvent> cometEvent = new ThreadLocal<HttpEvent>();

   public void event(HttpEvent event) throws IOException, ServletException
   {
      HttpServletRequest request = event.getHttpServletRequest();
      HttpServletResponse response = event.getHttpServletResponse();
      switch (event.getType())
      {
         case BEGIN:
         {
            try
            {
               cometEvent.set(event);
               super.service(request.getMethod(), request, response);
            }
            finally
            {
               cometEvent.set(null);
            }
            break;
         }
         case ERROR:
         case EOF:
         case TIMEOUT:
         {
            event.close();
            break;
         }
      }
   }

   @Override
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse)
   {
      return new JBossWebAsyncHttpRequest(httpServletRequest, httpResponse, httpHeaders, uriInfo, httpMethod, (SynchronousDispatcher) getDispatcher(), cometEvent.get());
   }
}
