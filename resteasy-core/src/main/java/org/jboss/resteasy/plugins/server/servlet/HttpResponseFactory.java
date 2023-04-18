package org.jboss.resteasy.plugins.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.HttpResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpResponseFactory {
    HttpResponse createResteasyHttpResponse(HttpServletResponse response, HttpServletRequest request);
}
