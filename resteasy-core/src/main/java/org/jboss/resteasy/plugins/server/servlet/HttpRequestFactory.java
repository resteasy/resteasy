package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequestFactory
{
   HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request, ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response);
}
