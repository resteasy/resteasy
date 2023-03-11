package org.jboss.resteasy.plugins.server.sun.http;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.util.HttpHeaderNames;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthFilter extends Filter {
    protected SecurityDomain domain;

    public BasicAuthFilter(final SecurityDomain domain) {
        this.domain = domain;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String auth = exchange.getRequestHeaders().getFirst(HttpHeaderNames.AUTHORIZATION);
        if (auth != null && auth.length() > 5) {
            String type = auth.substring(0, 5);
            type = type.toLowerCase();
            if ("basic".equals(type)) {
                String cookie = auth.substring(6);
                cookie = new String(Base64.getDecoder().decode(cookie.getBytes()));
                String[] split = cookie.split(":");
                //System.out.println("Authenticating user: " + split[0] + " passwd: " + split[1]);
                Principal user = null;
                try {
                    user = domain.authenticate(split[0], split[1]);
                } catch (SecurityException e) {
                    exchange.sendResponseHeaders(HttpResponseCodes.SC_UNAUTHORIZED, -1);
                    return;
                }

                final Principal finalUser = user;

                SecurityContext securityContext = new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return finalUser;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return domain.isUserInRole(getUserPrincipal(), role);
                    }

                    @Override
                    public boolean isSecure() {
                        return true;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return "BASIC";
                    }
                };

                try {
                    ResteasyContext.pushContext(SecurityContext.class, securityContext);
                    chain.doFilter(exchange);
                    return;
                } finally {
                    ResteasyContext.clearContextData();
                }

            }
        }
        exchange.sendResponseHeaders(HttpResponseCodes.SC_UNAUTHORIZED, -1);
    }

    @Override
    public String description() {
        return "Basic Auth Filter";
    }
}
