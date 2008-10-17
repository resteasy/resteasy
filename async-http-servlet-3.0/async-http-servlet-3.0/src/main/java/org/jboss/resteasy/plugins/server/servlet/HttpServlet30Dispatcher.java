package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServlet30Dispatcher extends HttpServletDispatcher
{
   @Override
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse)
   {
      try
      {
         return new Servlet3AsyncHttpRequest(httpServletRequest, httpResponse, httpHeaders, httpServletRequest.getInputStream(), uriInfo, httpMethod, (SynchronousDispatcher)dispatcher);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
