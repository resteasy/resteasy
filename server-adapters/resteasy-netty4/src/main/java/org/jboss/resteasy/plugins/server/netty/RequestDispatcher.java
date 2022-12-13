package org.jboss.resteasy.plugins.server.netty;

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

import io.netty.channel.ChannelHandlerContext;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Norman Maurer
 * @version $Revision: 1 $
 */
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

    public void service(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response, boolean handleNotFound)
            throws IOException {

        try {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.push(providerFactory);
            }

            SecurityContext securityContext;
            if (domain != null) {
                securityContext = basicAuthentication(request, response);
                if (securityContext == null) // not authenticated
                {
                    return;
                }
            } else {
                securityContext = new NettySecurityContext();
            }
            try {

                ResteasyContext.pushContext(SecurityContext.class, securityContext);
                ResteasyContext.pushContext(ChannelHandlerContext.class, ctx);
                if (handleNotFound) {
                    dispatcher.invoke(request, response);
                } else {
                    dispatcher.invokePropagateNotFound(request, response);
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

    private SecurityContext basicAuthentication(HttpRequest request, HttpResponse response) throws IOException {
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
                        return new NettySecurityContext(user, domain, "BASIC", true);
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
