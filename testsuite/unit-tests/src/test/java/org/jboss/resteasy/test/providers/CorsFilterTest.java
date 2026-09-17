/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.providers;

import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.jboss.resteasy.spi.CorsHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests the CORS headers written by the {@link CorsFilter}. A wildcard origin must be returned literally instead of
 * reflecting the origin of the request, credentials are never sent with a wildcard origin and credentials are disabled
 * unless they are explicitly enabled.
 * <p>
 * The filter is registered once as a singleton, like it is in a real deployment. Each test starts from the
 * configuration set up in {@link #setupSecureFilter()} and reconfigures it if a different one is required.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(application = CorsFilterTest.TestApplication.class)
class CorsFilterTest {

    private static final CorsFilter CORS_FILTER = new CorsFilter();

    @BeforeEach
    void setupSecureFilter() {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("https://trusted-frontend.com");
        CORS_FILTER.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS");
        // We set this to true for this test to avoid having to set it for several tests
        CORS_FILTER.setAllowCredentials(true);
        // The filter is shared, reset the optional settings so a test cannot be affected by a previous one
        CORS_FILTER.setAllowedHeaders(null);
        CORS_FILTER.setExposedHeaders(null);
        CORS_FILTER.setCorsMaxAge(-1);
    }

    @Test
    void credentialsDisabledByDefault() {
        assertFalse(new CorsFilter().isAllowCredentials(),
                "Credentials must be disabled by default so endpoints are secure without additional configuration");
    }

    @Test
    void missingOriginHeaderSameOriginRequest(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", null, null, 200);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "A same origin request must not be given CORS headers");
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void trustedDomainGetActualRequest(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com", null, 200);
        assertEquals("https://trusted-frontend.com", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void trustedDomainGetCredentialsDisabled(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.setAllowCredentials(false);

        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com", null, 200);
        assertEquals("https://trusted-frontend.com", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS),
                "Credentials are disabled, so the header must not be sent");
    }

    /**
     * The resource selects a representation based on the request headers, so it already varies on those headers.
     * Adding {@code Vary: Origin} must not discard them, a cache which no longer sees the negotiation headers can
     * serve the wrong representation.
     */
    @Test
    void trustedDomainGetKeepsResourceVaryHeader(
            @RestResource @RequestPath("test/negotiated") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com", null, 200);
        assertEquals("https://trusted-frontend.com", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        final String vary = headers.get(HttpHeaders.VARY);
        assertTrue(vary.contains("Accept-Language"),
                () -> String.format(
                        "'Vary: Origin' must be added to the headers the resource varies on, not replace them: %s",
                        vary));
    }

    /**
     * The exposed headers are not part of the origin check, they must be sent whichever allowed origin matched.
     */
    @Test
    void exposedHeadersSentForTrustedDomain(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.setExposedHeaders("X-Total-Count, X-Page");

        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com", null, 200);
        assertEquals("X-Total-Count, X-Page", headers.get(ACCESS_CONTROL_EXPOSE_HEADERS));
    }

    /**
     * The exposed headers are not part of the origin check, they must be sent whichever allowed origin matched.
     */
    @Test
    void exposedHeadersSentForWildcardOrigin(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("*");
        CORS_FILTER.setExposedHeaders("X-Total-Count, X-Page");

        final Map<String, String> headers = executeCors(target, "GET", "https://random-site.com", null, 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("X-Total-Count, X-Page", headers.get(ACCESS_CONTROL_EXPOSE_HEADERS),
                "A wildcard origin must not stop the exposed headers being sent");
    }

    @Test
    void trustedDomainOptionsPreflight(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://trusted-frontend.com", "POST",
                200);
        assertEquals("https://trusted-frontend.com", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", headers.get(ACCESS_CONTROL_ALLOW_METHODS));
    }

    /**
     * With no allowed methods or headers configured the preflight response echoes back what the request asked for.
     * The maximum age is only sent once it has been configured.
     */
    @Test
    void preflightEchoesRequestedMethodAndHeaders(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.setAllowedMethods(null);
        CORS_FILTER.setCorsMaxAge(600);

        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://trusted-frontend.com", "PATCH",
                "X-Requested-With, Content-Type", 200);
        assertEquals("PATCH", headers.get(ACCESS_CONTROL_ALLOW_METHODS));
        assertEquals("X-Requested-With, Content-Type", headers.get(ACCESS_CONTROL_ALLOW_HEADERS));
        assertEquals("600", headers.get(ACCESS_CONTROL_MAX_AGE));
    }

    /**
     * Configured allowed headers replace the headers the request asked for, a client must not be able to widen
     * what the filter allows.
     */
    @Test
    void preflightReturnsConfiguredHeaders(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.setAllowedHeaders("Content-Type");

        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://trusted-frontend.com", "POST",
                "X-Requested-With, Content-Type", 200);
        assertEquals("Content-Type", headers.get(ACCESS_CONTROL_ALLOW_HEADERS));
        assertNull(headers.get(ACCESS_CONTROL_MAX_AGE),
                "The maximum age must not be sent unless it has been configured");
    }

    @Test
    void publicApiEndpointNoAuthNeeded(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("*");
        CORS_FILTER.setAllowCredentials(false);

        final Map<String, String> headers = executeCors(target, "GET", "https://random-site.com", null, 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void publicApiEndpointOptionsPreflight(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("*");
        CORS_FILTER.setAllowCredentials(false);

        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://random-site.com", "PUT", 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", headers.get(ACCESS_CONTROL_ALLOW_METHODS));
    }

    @Test
    void wildcardWithCredentialsSpecViolation(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("*");
        CORS_FILTER.setAllowCredentials(true);

        final Map<String, String> headers = executeCors(target, "GET", "https://some-domain.com", null, 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "A wildcard origin must be returned literally rather than reflecting the request origin");
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS),
                "Credentials cannot be combined with a wildcard origin");
    }

    @Test
    void wildcardWithCredentialsPreflightSpecViolation(@RestResource @RequestPath("test") final WebTarget target) {
        CORS_FILTER.getAllowedOrigins().clear();
        CORS_FILTER.getAllowedOrigins().add("*");
        CORS_FILTER.setAllowCredentials(true);

        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://some-domain.com", "POST", 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "A wildcard origin must be returned literally rather than reflecting the request origin");
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS),
                "Credentials cannot be combined with a wildcard origin on a preflight request either");
    }

    @Test
    void wildcardAppliesToExplicitlyAllowedOrigin(@RestResource @RequestPath("test") final WebTarget target) {
        // The trusted origin and credentials are already configured, adding a wildcard must not leave the trusted
        // origin with credentials
        CORS_FILTER.getAllowedOrigins().add("*");

        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com", null, 200);
        assertEquals("*", headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "A wildcard applies to every origin, including one which is also listed explicitly");
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS),
                "Credentials cannot be combined with a wildcard origin, even for an explicitly allowed origin");
    }

    @Test
    void reflectedAttackerExploit(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "https://evil.example.com", null, 403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "An untrusted origin must never be reflected");
    }

    /**
     * A sandboxed iframe, a redirect and a document loaded from a file all send {@code Origin: null}. It is not a
     * trusted origin and must never be added to the allowed origins to make a client work.
     */
    @Test
    void nullOriginExploit(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "null", null, 403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    /**
     * An attacker can use an allowed origin as a subdomain (prefix) of their own domain. Origins are compared as a
     * whole, never with a prefix or suffix match.
     */
    @Test
    void suffixedOriginExploit(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com.evil.example",
                null, 403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "An allowed origin used as a prefix of an attacker controlled domain must not match");
    }

    /**
     * The port is part of an origin, so a service listening on another port of an allowed host is a different
     * origin and is not covered by the allowed origins.
     */
    @Test
    void mismatchedPortExploit(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET", "https://trusted-frontend.com:8443", null,
                403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "An origin on a different port is a different origin");
    }

    /**
     * A request carrying more than one {@code Origin} header is read as a single comma separated value. The value
     * is matched as a whole, an allowed origin in the list must not let the rest of the list through.
     */
    @Test
    void multipleOriginsExploit(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "GET",
                "https://trusted-frontend.com, https://evil.example.com", null, 403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN),
                "A list of origins must not match on the trusted entry alone");
    }

    @Test
    void untrustedDomainOptionsPreflight(@RestResource @RequestPath("test") final WebTarget target) {
        final Map<String, String> headers = executeCors(target, "OPTIONS", "https://hacker.network", "DELETE", 403);
        assertNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    /**
     * Executes the request and asserts the invariants which must hold for every response, whatever the filter is
     * configured to allow. Scenario specific expectations are left to the caller.
     */
    private Map<String, String> executeCors(final WebTarget target, final String httpMethod, final String origin,
            final String requestMethod, final int expectedStatus) {
        return executeCors(target, httpMethod, origin, requestMethod, null, expectedStatus);
    }

    private Map<String, String> executeCors(final WebTarget target, final String httpMethod, final String origin,
            final String requestMethod, final String requestHeaders, final int expectedStatus) {
        final Builder requestBuilder = target.request();

        if (origin != null) {
            requestBuilder.header("Origin", origin);
        }

        if (requestMethod != null) {
            requestBuilder.header("Access-Control-Request-Method", requestMethod);
        }

        if (requestHeaders != null) {
            requestBuilder.header("Access-Control-Request-Headers", requestHeaders);
        }

        try (Response response = requestBuilder.method(httpMethod)) {
            assertEquals(expectedStatus, response.getStatus(),
                    () -> String.format("Invalid status of %d: %s", response.getStatus(), response.readEntity(String.class)));
            final String allowOrigin = response.getHeaderString(ACCESS_CONTROL_ALLOW_ORIGIN);
            final String vary = response.getHeaderString("Vary");

            assertFalse(
                    "*".equals(allowOrigin)
                            && "true".equals(response.getHeaderString(ACCESS_CONTROL_ALLOW_CREDENTIALS)),
                    "A wildcard Access-Control-Allow-Origin must never be sent with Access-Control-Allow-Credentials");

            // Caches must be told to vary by origin if, and only if, a concrete origin was echoed back
            if (allowOrigin != null && !"*".equals(allowOrigin)) {
                assertTrue(vary != null && vary.contains("Origin"),
                        () -> String.format("Missing 'Vary: Origin' for echoed origin %s", allowOrigin));
            } else {
                assertFalse(vary != null && vary.contains("Origin"),
                        () -> String.format("Unexpected 'Vary: Origin' for Access-Control-Allow-Origin %s", allowOrigin));
            }
            final MultivaluedMap<String, String> headers = response.getStringHeaders();
            // Use a case-insensitive map
            final Map<String, String> flattened = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            headers.forEach((key, value) -> flattened.put(key, response.getHeaderString(key)));
            return flattened;
        }
    }

    @Path("/test")
    @Consumes(MediaType.TEXT_PLAIN)
    public static class TestResource {

        @GET
        public String get() {
            return "ok";
        }

        @GET
        @Path("negotiated")
        public Response negotiated() {
            return Response.ok("ok")
                    .variants(Variant.languages(Locale.ENGLISH, Locale.FRENCH).build())
                    .build();
        }
    }

    @Provider
    public static class CorsFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            context.register(CORS_FILTER);
            return true;
        }
    }

    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class, CorsFeature.class);
        }
    }
}
