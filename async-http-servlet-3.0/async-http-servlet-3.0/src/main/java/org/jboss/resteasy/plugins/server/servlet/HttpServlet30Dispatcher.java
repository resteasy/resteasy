package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServlet30Dispatcher extends HttpServletDispatcher
{
   @Override
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse)
   {
      return new Servlet3AsyncHttpRequest(httpServletRequest, httpResponse, httpHeaders, uriInfo, httpMethod, (SynchronousDispatcher) dispatcher);
   }
}
