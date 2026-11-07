/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.interception;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests {@code getHeaderString(String, String)} on {@link HttpHeaders}, {@link ContainerRequestContext},
 * {@link ContainerResponseContext}, {@link ClientRequestContext} and {@link ClientResponseContext}.
 * <p>
 * Each request sends the header once per value, and the resource sends the values back the same way. This means the
 * values must arrive as separate values for the separator to be used. Only {@link ContainerResponseContext} and
 * {@link ClientRequestContext} hold objects, the other types only see the strings which were sent. The objects are
 * tested with {@link ObjectValues}. A filter can also add values to the headers of {@link ContainerRequestContext} and
 * {@link ClientResponseContext}, so the {@link ObjectValues} which only hold strings are tested with those too.
 * </p>
 *
 * @since 8.0
 */
@RestBootstrap({ HeaderStringTest.HeaderStringResource.class, HeaderStringTest.HeaderStringFilter.class,
        HeaderStringTest.CustomValueDelegate.class })
@Tag("cdi")
class HeaderStringTest {
    private static final String HEADER = "test-header";
    private static final String HTTP_HEADERS_RESULT = "test-http-headers-result";
    private static final String CONTAINER_REQUEST_RESULT = "test-container-request-result";
    private static final String CONTAINER_RESPONSE_RESULT = "test-container-response-result";

    @RestResource
    @RequestPath("header-string")
    private WebTarget target;

    /**
     * The header is read from the {@link HttpHeaders} injected into the resource.
     */
    @ParameterizedTest
    @MethodSource("repeatedValues")
    void httpHeaders(final List<String> values, final String name, final String separator, final String expected) {
        try (Response response = send(target, values, name, separator)) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, response.getHeaderString(HTTP_HEADERS_RESULT), row(values, name, separator));
        }
    }

    /**
     * The header is read from the {@link ContainerRequestContext} in a request filter.
     */
    @ParameterizedTest
    @MethodSource("repeatedValues")
    void containerRequestContext(final List<String> values, final String name, final String separator,
            final String expected) {
        try (Response response = send(target, values, name, separator)) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, response.getHeaderString(CONTAINER_REQUEST_RESULT), row(values, name, separator));
        }
    }

    /**
     * The header is read from the {@link ContainerResponseContext} in a response filter.
     */
    @ParameterizedTest
    @MethodSource("repeatedValues")
    void containerResponseContext(final List<String> values, final String name, final String separator,
            final String expected) {
        try (Response response = send(target, values, name, separator)) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, response.getHeaderString(CONTAINER_RESPONSE_RESULT),
                    row(values, name, separator));
        }
    }

    /**
     * The header is read from the {@link ClientRequestContext} in a client request filter.
     */
    @ParameterizedTest
    @MethodSource("repeatedValues")
    void clientRequestContext(final List<String> values, final String name, final String separator, final String expected) {
        final ClientHeaderReader reader = new ClientHeaderReader(name, separator);
        try (Response response = send(target.register(reader), values, name, separator)) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, reader.requestValue, row(values, name, separator));
        }
    }

    /**
     * The header is read from the {@link ClientResponseContext} in a client response filter.
     */
    @ParameterizedTest
    @MethodSource("repeatedValues")
    void clientResponseContext(final List<String> values, final String name, final String separator, final String expected) {
        final ClientHeaderReader reader = new ClientHeaderReader(name, separator);
        try (Response response = send(target.register(reader), values, name, separator)) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, reader.responseValue, row(values, name, separator));
        }
    }

    /**
     * The objects are set on the {@link ContainerResponseContext} in a response filter, and read back from it.
     */
    @ParameterizedTest
    @EnumSource
    void containerResponseContextObjectValues(final ObjectValues values) {
        try (Response response = target.queryParam("values", values.name()).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(values.expected, response.readEntity(String.class), values::name);
        }
    }

    /**
     * The objects are set on the {@link ClientRequestContext} in a client request filter, and read back from it.
     */
    @ParameterizedTest
    @EnumSource
    void clientRequestContextObjectValues(final ObjectValues values) {
        final ClientObjectValuesReader reader = new ClientObjectValuesReader(values);
        try (Response response = target.register(CustomValueDelegate.class).register(reader).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(values.expected, reader.value, values::name);
        }
    }

    /**
     * The values are set on the {@link ContainerRequestContext} in a request filter, and read back from it.
     */
    @ParameterizedTest
    @EnumSource(names = { "NO_VALUE", "NULL_VALUE", "NULL_BETWEEN_VALUES" })
    void containerRequestContextObjectValues(final ObjectValues values) {
        try (Response response = target.queryParam("requestValues", values.name()).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(values.expected, response.readEntity(String.class), values::name);
        }
    }

    /**
     * The values are set on the {@link ClientResponseContext} in a client response filter, and read back from it.
     */
    @ParameterizedTest
    @EnumSource(names = { "NO_VALUE", "NULL_VALUE", "NULL_BETWEEN_VALUES" })
    void clientResponseContextObjectValues(final ObjectValues values) {
        final ClientResponseValuesReader reader = new ClientResponseValuesReader(values);
        try (Response response = target.register(reader).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(values.expected, reader.value, values::name);
        }
    }

    /**
     * The client folds the {@code Accept} values into a single value before the context sees them. There is only one
     * value to join, so the separator is not used.
     */
    @Test
    void clientRequestContextFoldedAccept() {
        final ClientHeaderReader reader = new ClientHeaderReader(HttpHeaders.ACCEPT, ";");
        try (Response response = target.register(reader).request().accept("text/plain", "text/html").get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals("text/plain, text/html", reader.requestValue);
        }
    }

    private static Stream<Arguments> repeatedValues() {
        return Stream.of(
                Arguments.of(List.of("a", "b", "c"), HEADER, ";", "a;b;c"),
                Arguments.of(List.of("a", "b", "c"), HEADER, "|", "a|b|c"),
                Arguments.of(List.of("a", "b", "c"), HEADER, ",", "a,b,c"),
                Arguments.of(List.of("a", "b", "c"), HEADER, ", ", "a, b, c"),
                Arguments.of(List.of("a", "b", "c"), HEADER, "&", "a&b&c"),
                Arguments.of(List.of("a", "b", "c"), HEADER, "->", "a->b->c"),
                Arguments.of(List.of("a", "b"), HEADER, "", "ab"),
                Arguments.of(List.of("a"), HEADER, ";", "a"),
                Arguments.of(List.of("a,b", "c"), HEADER, ";", "a,b;c"),
                Arguments.of(List.of("a", "b"), HEADER.toUpperCase(Locale.ROOT), ";", "a;b"),
                Arguments.of(List.of("a"), "test-missing-header", ";", null),
                // No separator uses the method which does not accept a separator
                Arguments.of(List.of("a", "b"), HEADER, null, "a,b"));
    }

    private static Supplier<String> row(final List<String> values, final String name, final String separator) {
        return () -> "values=%s, name=%s, separator='%s'".formatted(values, name, separator);
    }

    private static Response send(final WebTarget target, final List<String> values, final String name,
            final String separator) {
        WebTarget request = target.queryParam("name", name);
        if (separator != null) {
            request = request.queryParam("separator",
                    URLEncoder.encode(separator, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        final Invocation.Builder builder = request.request();
        values.forEach(value -> builder.header(HEADER, value));
        return builder.get();
    }

    private static String read(final BiFunction<String, String, String> withSeparator,
            final Function<String, String> withoutSeparator, final String name, final String separator) {
        return separator == null ? withoutSeparator.apply(name) : withSeparator.apply(name, separator);
    }

    /**
     * Objects set as the values of a header on a context, and the expected result when joined with a semicolon.
     */
    public enum ObjectValues {
        NO_VALUE("", List.of()),
        NULL_VALUE("", Collections.singletonList(null)),
        NULL_BETWEEN_VALUES("a;;b", Arrays.asList("a", null, "b")),
        BUILT_IN_DELEGATE("en-US;5", List.of(Locale.US, 5)),
        CUSTOM_DELEGATE("custom", List.of(new CustomValue("custom"))),
        NULL_FROM_DELEGATE("", List.of(new CustomValue(null)));

        private final List<Object> values;
        private final String expected;

        ObjectValues(final String expected, final List<Object> values) {
            this.values = values;
            this.expected = expected;
        }

        private List<String> stringValues() {
            return values.stream().map(String.class::cast).toList();
        }
    }

    /**
     * A value whose {@code toString()} differs from the result of {@link CustomValueDelegate}.
     */
    public static class CustomValue {
        private final String value;

        CustomValue(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "toString() was used";
        }
    }

    @Provider
    public static class CustomValueDelegate implements RuntimeDelegate.HeaderDelegate<CustomValue> {

        @Override
        public CustomValue fromString(final String value) {
            return new CustomValue(value);
        }

        @Override
        public String toString(final CustomValue value) {
            return value.value;
        }
    }

    /**
     * Sends back the values of the request header, and the result of reading the header from {@link HttpHeaders}.
     */
    @Path("header-string")
    @Produces(MediaType.TEXT_PLAIN)
    public static class HeaderStringResource {

        @Inject
        private HttpHeaders headers;

        @GET
        public Response get(@QueryParam("name") final String name, @QueryParam("separator") final String separator) {
            final Response.ResponseBuilder builder = Response.ok("ok");
            if (name != null) {
                final List<String> values = headers.getRequestHeader(HEADER);
                if (values != null) {
                    values.forEach(value -> builder.header(HEADER, value));
                }
                builder.header(HTTP_HEADERS_RESULT, read(headers::getHeaderString, headers::getHeaderString, name, separator));
            }
            return builder.build();
        }
    }

    /**
     * Returns the results of reading the header from the contexts as response headers. For {@link ObjectValues}, the
     * result is returned as the entity because a {@code null} or empty value should not be sent as a header.
     */
    @Provider
    public static class HeaderStringFilter implements ContainerRequestFilter, ContainerResponseFilter {
        private static final String REQUEST_RESULT_PROPERTY = HeaderStringFilter.class.getName() + ".requestResult";

        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            final MultivaluedMap<String, String> params = requestContext.getUriInfo().getQueryParameters();
            final String name = params.getFirst("name");
            if (name != null) {
                requestContext.setProperty(REQUEST_RESULT_PROPERTY, read(requestContext::getHeaderString,
                        requestContext::getHeaderString, name, params.getFirst("separator")));
            }
            final String requestValues = params.getFirst("requestValues");
            if (requestValues != null) {
                requestContext.getHeaders().put(HEADER, ObjectValues.valueOf(requestValues).stringValues());
                requestContext.setProperty(REQUEST_RESULT_PROPERTY, requestContext.getHeaderString(HEADER, ";"));
                requestContext.getHeaders().remove(HEADER);
            }
        }

        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
                throws IOException {
            final MultivaluedMap<String, String> params = requestContext.getUriInfo().getQueryParameters();
            final String name = params.getFirst("name");
            if (name != null) {
                addResult(responseContext, CONTAINER_REQUEST_RESULT, requestContext.getProperty(REQUEST_RESULT_PROPERTY));
                addResult(responseContext, CONTAINER_RESPONSE_RESULT, read(responseContext::getHeaderString,
                        responseContext::getHeaderString, name, params.getFirst("separator")));
            }
            final String values = params.getFirst("values");
            if (values != null) {
                responseContext.getHeaders().put(HEADER, ObjectValues.valueOf(values).values);
                final String result = responseContext.getHeaderString(HEADER, ";");
                responseContext.getHeaders().remove(HEADER);
                responseContext.setEntity(String.valueOf(result));
            }
            if (params.getFirst("requestValues") != null) {
                responseContext.setEntity(String.valueOf(requestContext.getProperty(REQUEST_RESULT_PROPERTY)));
            }
        }

        private static void addResult(final ContainerResponseContext responseContext, final String header,
                final Object result) {
            if (result != null) {
                responseContext.getHeaders().add(header, result);
            }
        }
    }

    /**
     * Records the header from the client request and response contexts.
     */
    public static class ClientHeaderReader implements ClientRequestFilter, ClientResponseFilter {
        private final String name;
        private final String separator;
        private String requestValue;
        private String responseValue;

        ClientHeaderReader(final String name, final String separator) {
            this.name = name;
            this.separator = separator;
        }

        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            requestValue = read(requestContext::getHeaderString, requestContext::getHeaderString, name, separator);
        }

        @Override
        public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
                throws IOException {
            responseValue = read(responseContext::getHeaderString, responseContext::getHeaderString, name, separator);
        }
    }

    /**
     * Records the result of reading the objects from the client request context. The request is aborted because a
     * {@code null} or empty value should not be sent as a header.
     */
    public static class ClientObjectValuesReader implements ClientRequestFilter {
        private final ObjectValues values;
        private String value;

        ClientObjectValuesReader(final ObjectValues values) {
            this.values = values;
        }

        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().put(HEADER, values.values);
            value = requestContext.getHeaderString(HEADER, ";");
            requestContext.abortWith(Response.ok().build());
        }
    }

    /**
     * Records the result of reading the values from the client response context.
     */
    public static class ClientResponseValuesReader implements ClientResponseFilter {
        private final ObjectValues values;
        private String value;

        ClientResponseValuesReader(final ObjectValues values) {
            this.values = values;
        }

        @Override
        public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
                throws IOException {
            responseContext.getHeaders().put(HEADER, values.stringValues());
            value = responseContext.getHeaderString(HEADER, ";");
        }
    }
}
