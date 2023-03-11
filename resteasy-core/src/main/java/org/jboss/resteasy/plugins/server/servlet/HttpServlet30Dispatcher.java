package org.jboss.resteasy.plugins.server.servlet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.servlet.annotation.WebServlet;
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
// Note. Setting value to "/RESTEASY_HttpServlet30Dispatcher" frees up the root "/" context
//       to serve static content. The path "/RESTEASY_HttpServlet30Dispatcher" will be installed
//       and it will lead to HttpServlet30Dispatcher, but no resources will be there and status
//       404 will be returned.
@WebServlet(asyncSupported = true, value = "/RESTEASY_HttpServlet30Dispatcher")
public class HttpServlet30Dispatcher extends HttpServletDispatcher {
    ScheduledExecutorService asyncCancelScheduler = Executors.newScheduledThreadPool(0); // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.

    @Override
    protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest,
            ResteasyHttpHeaders httpHeaders, ResteasyUriInfo uriInfo, HttpResponse httpResponse,
            HttpServletResponse httpServletResponse) {
        return new Servlet3AsyncHttpRequest(httpServletRequest, httpServletResponse, getServletContext(), httpResponse,
                httpHeaders, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) getDispatcher());
    }

    @Override
    protected HttpResponse createServletResponse(HttpServletResponse response, HttpServletRequest request) {
        return new HttpServletResponseWrapper(response, request, getDispatcher().getProviderFactory()) {
            @Override
            public void addNewCookie(NewCookie cookie) {
                outputHeaders.add(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
            }
        };
    }
}
