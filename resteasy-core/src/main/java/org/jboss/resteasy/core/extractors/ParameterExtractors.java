/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.extractors;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.PathSegment;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.util.Encode;

/**
 * Factory for {@link RequestParameterExtractor} instances. Provides a unified {@linkplain #of entry point} that dispatches
 * by annotation type, as well as individual factory methods for each Jakarta REST parameter annotation
 * ({@code @CookieParam}, {@code @FormParam}, {@code @HeaderParam}, {@code @MatrixParam}, {@code @PathParam},
 * {@code @QueryParam}).
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ParameterExtractors {

    /**
     * Creates a {@link RequestParameterExtractor} for the given annotation type by dispatching to the appropriate
     * {@code forXxxParam} method.
     *
     * @param injectionType  the target type to convert to
     * @param genericType    the generic type of the injection point
     * @param annotations    all annotations on the injection point
     * @param encode         {@code true} if the parameter value should be URL-encoded
     * @param paramName      the parameter name as declared in the annotation, or derived from the injection point
     * @param defaultValue   the default value when the parameter is absent, or {@code null}
     * @param annotationType the parameter annotation type (e.g. {@code PathParam.class})
     * @param factory        the provider factory for converter lookups
     *
     * @return the extractor for the given parameter
     *
     * @throws IllegalArgumentException if the annotation type is not supported
     */
    public static RequestParameterExtractor of(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final boolean encode, final String paramName, final String defaultValue,
            final Class<? extends Annotation> annotationType,
            final ResteasyProviderFactory factory) {
        if (CookieParam.class.equals(annotationType)) {
            return forCookieParam(injectionType, genericType, annotations, paramName, defaultValue, factory);
        }
        if (FormParam.class.equals(annotationType)) {
            return forFormParam(injectionType, genericType, annotations, encode, paramName, defaultValue, factory);
        }
        if (HeaderParam.class.equals(annotationType)) {
            return forHeaderParam(injectionType, genericType, annotations, paramName, defaultValue, factory);
        }
        if (MatrixParam.class.equals(annotationType)) {
            return forMatrixParam(injectionType, genericType, annotations, encode, paramName, defaultValue, factory);
        }
        if (PathParam.class.equals(annotationType)) {
            return forPathParam(injectionType, genericType, annotations, encode, paramName, defaultValue, factory);
        }
        if (QueryParam.class.equals(annotationType)) {
            return forQueryParam(injectionType, genericType, annotations, encode, paramName, defaultValue, factory);
        }

        throw Messages.MESSAGES.unsupportedAnnotation(annotationType.getName());
    }

    /**
     * Creates an extractor for {@code @CookieParam}.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param paramName     the cookie name
     * @param defaultValue  the default value when the cookie is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the cookie parameter extractor
     */
    public static RequestParameterExtractor forCookieParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findCookieParam(annotations);
        final StringParameterConverter extractor;
        if (injectionType.equals(Cookie.class)) {
            extractor = StringParameterConverter.of(injectionType, null, annotationType, paramName, defaultValue,
                    Set.of(), factory);
        } else {
            extractor = StringParameterConverter.of(injectionType, genericType, annotationType, paramName, defaultValue,
                    annotations, factory);
        }
        return request -> {
            final Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
            if (injectionType.equals(Cookie.class))
                return cookie;

            if (cookie == null)
                return extractor.extractValues(null);
            final List<String> values = new ArrayList<>();
            values.add(cookie.getValue());
            return extractor.extractValues(values);
        };
    }

    /**
     * Creates an extractor for {@code @FormParam}.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param encode        {@code true} if the parameter value should be URL-encoded
     * @param paramName     the form parameter name
     * @param defaultValue  the default value when the parameter is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the form parameter extractor
     */
    public static RequestParameterExtractor forFormParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final boolean encode, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findFormParam(annotations);

        final StringParameterConverter extractor;
        if (EntityPart.class.isAssignableFrom(injectionType) ||
                (List.class.isAssignableFrom(injectionType)
                        && Types.isGenericTypeInstanceOf(EntityPart.class, genericType))
                ||
                InputStream.class.isAssignableFrom(injectionType)) {
            extractor = null;
        } else {
            extractor = StringParameterConverter.of(injectionType, genericType,
                    annotationType,
                    paramName, defaultValue, annotations,
                    factory);
        }
        return request -> {
            // A @FormParam for multipart/form-data can be a String, InputStream or EntityPart. This type is handled specially.
            if (EntityPart.class.isAssignableFrom(injectionType)) {
                return request.getFormEntityPart(paramName).orElse(null);
            } else if (List.class.isAssignableFrom(injectionType)
                    && Types.isGenericTypeInstanceOf(EntityPart.class, genericType)) {
                return request.getFormEntityParts();
            } else if (InputStream.class.isAssignableFrom(injectionType)) {
                final Optional<EntityPart> part = request.getFormEntityPart(paramName);
                return part.map(EntityPart::getContent).orElse(null);
            } else if (String.class.isAssignableFrom(injectionType) &&
            // request.getHttpHeaders().getMediaType() may return null, but the isCompatible handles this check
                    MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(request.getHttpHeaders().getMediaType())) {
                final Optional<EntityPart> part = request.getFormEntityPart(paramName);
                return part.map(p -> {
                    try {
                        return p.getContent(String.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(Messages.MESSAGES.unableToExtractParameter(paramName, null), e);
                    }
                }).orElse(null);
            }
            List<String> list = request.getDecodedFormParameters().get(paramName);
            if (list != null && encode) {
                final List<String> encodedList = new ArrayList<>();
                for (String s : list) {
                    encodedList.add(Encode.encodeString(s));
                }
                list = encodedList;
            }
            // This will not be null at this stage. The checks above are used to ensure the extractor is only used
            // if it was previously initialized to a non-null value. We use the assertion to make IDE's happy
            assert extractor != null;
            return extractor.extractValues(list);
        };
    }

    /**
     * Creates an extractor for {@code @HeaderParam}. Extracts the named header values from the request and converts
     * them to the target type.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param paramName     the header name
     * @param defaultValue  the default value when the header is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the header parameter extractor
     */
    public static RequestParameterExtractor forHeaderParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findHeaderParam(annotations);

        final StringParameterConverter extractor = StringParameterConverter.of(injectionType, genericType,
                annotationType,
                paramName,
                defaultValue, annotations,
                factory);
        return request -> {
            final List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
            return extractor.extractValues(list);
        };
    }

    /**
     * Creates an extractor for {@code @MatrixParam}. Collects the named matrix parameter values from all path
     * segments and converts them to the target type.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param encode        {@code true} if the parameter value should be URL-encoded
     * @param paramName     the matrix parameter name
     * @param defaultValue  the default value when the parameter is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the matrix parameter extractor
     */
    public static RequestParameterExtractor forMatrixParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final boolean encode, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findMatrixParam(annotations);

        final StringParameterConverter extractor = StringParameterConverter.of(injectionType, genericType,
                annotationType,
                paramName, defaultValue, annotations,
                factory, NotFoundException::new);
        return request -> {
            final ArrayList<String> values = new ArrayList<>();
            for (PathSegment segment : request.getUri().getPathSegments(!encode)) {
                final List<String> matrixParams = segment.getMatrixParameters().get(paramName);
                if (matrixParams != null)
                    values.addAll(matrixParams);
            }
            if (values.isEmpty())
                return extractor.extractValues(null);
            else
                return extractor.extractValues(values);
        };
    }

    /**
     * Creates an extractor for {@code @PathParam}.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param encode        {@code true} if the parameter value should be URL-encoded
     * @param paramName     the path parameter name as declared in the {@code @Path} template
     * @param defaultValue  the default value when the parameter is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the path parameter extractor
     */
    public static RequestParameterExtractor forPathParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final boolean encode, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findPathParam(annotations);
        final boolean pathSegmentArray = injectionType.isArray() && injectionType.getComponentType()
                .equals(PathSegment.class);
        final boolean pathSegmentList = isPathSegmentList(injectionType, genericType);

        final StringParameterConverter extractor;
        if (pathSegmentArray || pathSegmentList || injectionType.equals(PathSegment.class)) {
            extractor = null;
        } else {
            extractor = StringParameterConverter.of(injectionType, genericType, annotationType,
                    paramName, defaultValue, annotations,
                    factory, NotFoundException::new);
        }

        return request -> {
            final ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();

            if (extractor == null) {
                List<PathSegment[]> pathSegments;
                if (encode) {
                    pathSegments = uriInfo.getEncodedPathParameterPathSegments().get(paramName);
                } else {
                    pathSegments = uriInfo.getPathParameterPathSegments().get(paramName);
                }
                if (pathSegments == null) {
                    throw new InternalServerErrorException(Messages.MESSAGES.unknownPathParam(paramName, uriInfo.getPath()));
                }
                final Deque<PathSegment> segmentList = new ArrayDeque<>();
                for (PathSegment[] array : pathSegments) {
                    segmentList.addAll(List.of(array));
                }
                if (pathSegmentArray) {
                    return segmentList.toArray(new PathSegment[0]);
                } else if (pathSegmentList) {
                    return new ArrayList<>(segmentList);
                } else {
                    return segmentList.getLast();
                }
            }
            final List<String> pathParameters = request.getUri().getPathParameters(!encode).get(paramName);
            if (pathParameters == null) {
                if (extractor.isCollectionOrArray()) {
                    return extractor.extractValues(null);
                } else {
                    return extractor.extractValue(null);
                }
            }
            if (extractor.isCollectionOrArray()) {
                return extractor.extractValues(pathParameters);
            }
            return extractor.extractValue(pathParameters.get(pathParameters.size() - 1));
        };
    }

    /**
     * Creates an extractor for {@code @QueryParam}. Extracts the named query parameter values from the request URI
     * and converts them to the target type.
     *
     * @param injectionType the target type to convert to
     * @param genericType   the generic type of the injection point
     * @param annotations   all annotations on the injection point
     * @param encode        {@code true} if the parameter value should be URL-encoded
     * @param paramName     the query parameter name
     * @param defaultValue  the default value when the parameter is absent, or {@code null}
     * @param factory       the provider factory for converter lookups
     *
     * @return the query parameter extractor
     */
    public static RequestParameterExtractor forQueryParam(final Class<?> injectionType, final Type genericType,
            final Set<Annotation> annotations, final boolean encode, final String paramName, final String defaultValue,
            final ResteasyProviderFactory factory) {
        final Class<? extends Annotation> annotationType = findQueryParam(annotations);
        final String encodedName = URLDecoder.decode(paramName, StandardCharsets.UTF_8);

        final StringParameterConverter extractor = StringParameterConverter.of(injectionType, genericType,
                annotationType,
                paramName, defaultValue, annotations,
                factory, NotFoundException::new);
        return request -> {
            final List<String> queryParameters;
            if (encode) {
                queryParameters = request.getUri().getQueryParameters(false).get(encodedName);
            } else {
                queryParameters = request.getUri().getQueryParameters().get(paramName);

            }
            return extractor.extractValues(queryParameters);
        };
    }

    private static boolean isPathSegmentList(final Class<?> type, final Type genericType) {
        Class<?> collectionBaseType = Types.getCollectionBaseType(type, genericType);
        return (List.class.equals(type) || ArrayList.class.equals(type)) && collectionBaseType != null
                && collectionBaseType.equals(PathSegment.class);
    }

    private static Class<? extends Annotation> findCookieParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, CookieParam.class, org.jboss.resteasy.annotations.jaxrs.CookieParam.class);
    }

    private static Class<? extends Annotation> findHeaderParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, HeaderParam.class, org.jboss.resteasy.annotations.jaxrs.HeaderParam.class);
    }

    private static Class<? extends Annotation> findFormParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, FormParam.class, org.jboss.resteasy.annotations.jaxrs.FormParam.class);
    }

    private static Class<? extends Annotation> findMatrixParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, MatrixParam.class, org.jboss.resteasy.annotations.jaxrs.MatrixParam.class);
    }

    private static Class<? extends Annotation> findPathParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, PathParam.class, org.jboss.resteasy.annotations.jaxrs.PathParam.class);
    }

    private static Class<? extends Annotation> findQueryParam(final Set<Annotation> annotations) {
        return findParamAnnotationType(annotations, QueryParam.class, org.jboss.resteasy.annotations.jaxrs.QueryParam.class);
    }

    private static Class<? extends Annotation> findParamAnnotationType(final Set<Annotation> annotations,
            final Class<? extends Annotation> specAnnotation,
            final Class<? extends Annotation> resteasyAnnotation) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == specAnnotation
                    || annotation.annotationType() == resteasyAnnotation) {
                return annotation.annotationType();
            }
        }
        return null;
    }
}
