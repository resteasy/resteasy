package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequestFactory
{
   HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request, HttpHeaders headers, UriInfoImpl uriInfo, HttpResponse theResponse);
}
