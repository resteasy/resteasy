package org.jboss.resteasy.plugins.server.servlet;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletSecurityContext implements SecurityContext {
    private HttpServletRequest request;

    public ServletSecurityContext(final HttpServletRequest request) {
        this.request = request;
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public String getAuthenticationScheme() {
        return request.getAuthType();
    }
}
