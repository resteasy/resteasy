package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpResponseFactory
{
   HttpResponse createResteasyHttpResponse(HttpServletResponse response, HttpServletRequest request);
}
