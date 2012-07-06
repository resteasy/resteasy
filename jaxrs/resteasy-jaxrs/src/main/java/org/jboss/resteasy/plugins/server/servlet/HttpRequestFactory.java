package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequestFactory
{
   HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request, HttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response);
}
