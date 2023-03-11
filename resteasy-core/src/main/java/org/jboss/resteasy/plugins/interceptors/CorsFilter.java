package org.jboss.resteasy.plugins.interceptors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.CorsHeaders;

/**
 * Handles CORS requests both preflight and simple CORS requests.
 * You must bind this as a singleton and set up allowedOrigins and other settings to use.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {
    protected boolean allowCredentials = true;
    protected String allowedMethods;
    protected String allowedHeaders;
    protected String exposedHeaders;
    protected int corsMaxAge = -1;
    protected Set<String> allowedOrigins = new HashSet<String>();

    /**
     * Put "*" if you want to accept all origins.
     *
     * @return allowed origins
     */
    public Set<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Defaults to true.
     *
     * @return allow credentials
     */
    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    /**
     * Will allow all by default.
     *
     * @return allowed methods
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Will allow all by default
     * comma delimited string for Access-Control-Allow-Methods.
     *
     * @param allowedMethods allowed methods
     */
    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * Will allow all by default
     * comma delimited string for Access-Control-Allow-Headers.
     *
     * @param allowedHeaders allowed headers
     */
    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public int getCorsMaxAge() {
        return corsMaxAge;
    }

    public void setCorsMaxAge(int corsMaxAge) {
        this.corsMaxAge = corsMaxAge;
    }

    public String getExposedHeaders() {
        return exposedHeaders;
    }

    /**
     * Comma delimited list.
     *
     * @param exposedHeaders exposed headers
     */
    public void setExposedHeaders(String exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String origin = requestContext.getHeaderString(CorsHeaders.ORIGIN);
        if (origin == null) {
            return;
        }
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            preflight(origin, requestContext);
        } else {
            checkOrigin(requestContext, origin);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString(CorsHeaders.ORIGIN);
        if (origin == null || requestContext.getMethod().equalsIgnoreCase("OPTIONS")
                || requestContext.getProperty("cors.failure") != null) {
            // don't do anything if origin is null, its an OPTIONS request, or cors.failure is set
            return;
        }
        responseContext.getHeaders().putSingle(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        responseContext.getHeaders().putSingle(CorsHeaders.VARY, CorsHeaders.ORIGIN);
        if (isAllowCredentials())
            responseContext.getHeaders().putSingle(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        if (getExposedHeaders() != null) {
            responseContext.getHeaders().putSingle(CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, getExposedHeaders());
        }
    }

    protected void preflight(String origin, ContainerRequestContext requestContext) throws IOException {
        checkOrigin(requestContext, origin);

        Response.ResponseBuilder builder = Response.ok();
        builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        builder.header(CorsHeaders.VARY, CorsHeaders.ORIGIN);
        if (isAllowCredentials())
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        String requestMethods = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        if (requestMethods != null) {
            if (getAllowedMethods() != null) {
                requestMethods = getAllowedMethods();
            }
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
        }
        String allowHeaders = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        if (allowHeaders != null) {
            if (getAllowedHeaders() != null) {
                allowHeaders = getAllowedHeaders();
            }
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        }
        if (getCorsMaxAge() > -1) {
            builder.header(CorsHeaders.ACCESS_CONTROL_MAX_AGE, getCorsMaxAge());
        }
        requestContext.abortWith(builder.build());

    }

    protected void checkOrigin(ContainerRequestContext requestContext, String origin) {
        if (!getAllowedOrigins().contains("*") && !getAllowedOrigins().contains(origin)) {
            requestContext.setProperty("cors.failure", true);
            throw new ForbiddenException(Messages.MESSAGES.originNotAllowed(origin));
        }
    }
}
