package org.jboss.resteasy.plugins.server.vertx;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Norman Maurer
 * @version $Revision: 1 $
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class RequestDispatcher {
    protected final SynchronousDispatcher dispatcher;
    protected final ResteasyProviderFactory providerFactory;
    protected final SecurityDomain domain;

    public RequestDispatcher(final SynchronousDispatcher dispatcher, final ResteasyProviderFactory providerFactory,
            final SecurityDomain domain) {
        this.dispatcher = dispatcher;
        this.providerFactory = providerFactory;
        this.domain = domain;
    }

    public SynchronousDispatcher getDispatcher() {
        return dispatcher;
    }

    public SecurityDomain getDomain() {
        return domain;
    }

    public ResteasyProviderFactory getProviderFactory() {
        return providerFactory;
    }

    public void service(Context context,
            HttpServerRequest req,
            HttpServerResponse resp,
            HttpRequest vertxReq, HttpResponse vertxResp, boolean handleNotFound) throws IOException {

        try {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.push(providerFactory);
            }

            SecurityContext securityContext;
            if (domain != null) {
                securityContext = authenticate(vertxReq, vertxResp);
                if (securityContext == null) // not authenticated
                {
                    return;
                }
            } else {
                securityContext = new VertxSecurityContext();
            }
            try {

                ResteasyContext.pushContext(SecurityContext.class, securityContext);
                ResteasyContext.pushContext(Context.class, context);
                ResteasyContext.pushContext(HttpServerRequest.class, req);
                ResteasyContext.pushContext(HttpServerResponse.class, resp);
                ResteasyContext.pushContext(Vertx.class, context.owner());
                if (handleNotFound) {
                    dispatcher.invoke(vertxReq, vertxResp);
                } else {
                    dispatcher.invokePropagateNotFound(vertxReq, vertxResp);
                }
            } finally {
                ResteasyContext.clearContextData();
            }
        } finally {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.pop();
            }

        }
    }

    protected SecurityContext authenticate(HttpRequest request, HttpResponse response) throws IOException {
        List<String> headers = request.getHttpHeaders().getRequestHeader(HttpHeaderNames.AUTHORIZATION);
        if (!headers.isEmpty()) {
            String auth = headers.get(0);
            if (auth.length() > 5) {
                String type = auth.substring(0, 5);
                type = type.toLowerCase();
                if ("basic".equals(type)) {
                    String cookie = auth.substring(6);
                    cookie = new String(Base64.getDecoder().decode(cookie.getBytes()));
                    String[] split = cookie.split(":");
                    Principal user = null;
                    try {
                        user = domain.authenticate(split[0], split[1]);
                        return new VertxSecurityContext(user, domain, "BASIC", true);
                    } catch (SecurityException e) {
                        response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
                        return null;
                    }
                } else {
                    response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
                    return null;
                }
            }
        }
        return null;
    }

}
