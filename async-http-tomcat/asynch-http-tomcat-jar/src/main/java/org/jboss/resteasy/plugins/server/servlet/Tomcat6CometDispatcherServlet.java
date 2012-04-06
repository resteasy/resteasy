package org.jboss.resteasy.plugins.server.servlet;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Tomcat6CometDispatcherServlet extends HttpServletDispatcher implements CometProcessor
{
   // Hack to avoid code changes within HttpServletDispatcher
   private static ThreadLocal<CometEvent> cometEvent = new ThreadLocal<CometEvent>();

   public void event(CometEvent event) throws IOException, ServletException
   {
      HttpServletRequest request = event.getHttpServletRequest();
      HttpServletResponse response = event.getHttpServletResponse();
      if (event.getEventType() == CometEvent.EventType.BEGIN)
      {
         cometEvent.set(event);
         try
         {
            super.service(request.getMethod(), request, response);
         }
         finally
         {
            cometEvent.set(null);
         }
      }
      else if (event.getEventType() == CometEvent.EventType.ERROR)
      {
         event.close();
      }
      else if (event.getEventType() == CometEvent.EventType.END || event.getEventSubType() == CometEvent.EventSubType.TIMEOUT)
      {
         event.close();
      }
   }

   @Override
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse, HttpServletResponse response)
   {
      return new Tomcat6AsyncHttpRequest(httpServletRequest, httpResponse, httpHeaders, uriInfo, httpMethod, (SynchronousDispatcher) getDispatcher(), cometEvent.get());
   }
}
