package org.jboss.resteasy.plugins.server.servlet;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.NewCookie;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@WebFilter(asyncSupported = true, value = "")
public class Filter30Dispatcher extends FilterDispatcher {
    @Override
    public HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest httpServletRequest,
            ResteasyHttpHeaders httpHeaders, ResteasyUriInfo uriInfo, HttpResponse httpResponse,
            HttpServletResponse httpServletResponse) {
        return new Servlet3AsyncHttpRequest(httpServletRequest, httpServletResponse, servletContext, httpResponse, httpHeaders,
                uriInfo, httpMethod, (SynchronousDispatcher) getDispatcher());
    }

    @Override
    public HttpResponse createResteasyHttpResponse(HttpServletResponse response, HttpServletRequest request) {
        return new HttpServletResponseWrapper(response, request, getDispatcher().getProviderFactory()) {
            @Override
            public void addNewCookie(NewCookie cookie) {
                outputHeaders.add(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
            }
        };
    }

}
