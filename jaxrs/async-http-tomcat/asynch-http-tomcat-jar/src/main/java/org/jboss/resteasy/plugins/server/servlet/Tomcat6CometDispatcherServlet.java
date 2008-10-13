package org.jboss.resteasy.plugins.server.servlet;

import org.apache.catalina.CometProcessor;
import org.apache.catalina.CometEvent;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.ws.rs.core.HttpHeaders;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Tomcat6CometDispatcherServlet extends HttpServletDispatcher implements CometProcessor
{
   public void event(CometEvent event) throws IOException, ServletException
   {
      HttpServletRequest request = event.getHttpServletRequest();
      HttpServletResponse response = event.getHttpServletResponse();
      if (event.getEventType() == CometEvent.EventType.BEGIN)
      {
         super.service(request.getMethod(), request, response);
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
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse)
   {
      try
      {
         return new AsyncHttpRequest(httpServletRequest, httpResponse, httpHeaders, httpServletRequest.getInputStream(), uriInfo, httpMethod, (SynchronousDispatcher)dispatcher);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
