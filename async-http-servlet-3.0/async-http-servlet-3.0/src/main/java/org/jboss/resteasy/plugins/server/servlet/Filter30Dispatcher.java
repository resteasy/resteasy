package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@WebFilter(asyncSupported = true)
public class Filter30Dispatcher extends FilterDispatcher
{
   @Override
   public HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, UriInfoImpl uriInfo, HttpResponse httpResponse, HttpServletResponse httpServletResponse)
   {
      return new Servlet3AsyncHttpRequest(httpServletRequest, httpServletResponse, httpResponse, httpHeaders, uriInfo, httpMethod, (SynchronousDispatcher) getDispatcher());
   }

}
