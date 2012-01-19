/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Set;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Defines the contract between a returned instance and the runtime when
 * an application needs to provide metadata to the runtime. An application
 * class can extend this class directly or can use one of the static
 * methods to create an instance using a ResponseBuilder.
 * <p />
 * Several methods have parameters of type URI, {@link UriBuilder} provides
 * convenient methods to create such values as does {@link URI#create(java.lang.String)}.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @author Marek Potociar
 * @see Response.ResponseBuilder
 * @since 1.0
 */
public abstract class Response {

    /**
     * Protected constructor, use one of the static methods to obtain a
     * {@link ResponseBuilder} instance and obtain a Response from that.
     */
    protected Response() {
    }

    /**
     * Get the map of response properties.
     * <p>
     * A response property is an application-defined property that may be
     * added by the user, a filter, or the handler that is managing the
     * connection.
     *
     * @return the map of response properties.
     * @since 2.0
     */
    public abstract Map<String, Object> getProperties();

    /**
     * Get the status code associated with the response.
     *
     * @return the response status code or -1 if the status was not set.
     */
    public abstract int getStatus();

    /**
     * Get the response status represented as a response {@link Status} enumeration
     * value.
     *
     * @return the status type instance, or {@code null} if there is no
     * mapping between the integer status code and the
     * {@link javax.ws.rs.core.Response.Status response status enumeration} value.
     * @since 2.0
     */
    public abstract Status getStatusEnum();

    /**
     * Get the response message headers. This method never returns {@code null}.
     *
     * @return response message headers. Returned headers may be empty but never
     *     {@code null}.
     * @see javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate
     *
     * @since 2.0
     */
    public abstract ResponseHeaders getHeaders();

    /**
     * Get the message entity, returns {@code null} if the message does not
     * contain an entity body.
     * <p/>
     * Upon sending, the response will be serialized using a {@link javax.ws.rs.ext.MessageBodyWriter}
     * for either the class of the entity or, in the case of {@link GenericEntity},
     * the value of {@link GenericEntity#getRawType()}.
     *
     * @return the message entity or {@code null} if there is no entity.
     * @see javax.ws.rs.ext.MessageBodyWriter
     */
    public abstract Object getEntity();

    /**
     * Get the message entity, returns {@code null} if the message does not
     * contain an entity body.
     *
     * Entity can also be retrieved as an {@link java.io.InputStream}, in which
     * case it will be fully consumed once the reading from input stream is finished.
     * All subsequent calls to {@code getEntity(...)} on the same response instance
     * will result in a {@link MessageProcessingException} being thrown. It is up
     * to the consumer of the entity input stream to ensure that consuming the stream
     * is properly mitigated (e.g. by substituting the consumed response instance
     * with a new one etc.).
     *
     * @param <T> entity type.
     * @param type the type of entity.
     * @return the message entity or {@code null}.
     * @throws MessageProcessingException if the content of the message
     *     cannot be mapped to an entity of the requested type.
     * @see #hasEntity()
     * @since 2.0
     */
    public abstract <T> T getEntity(Class<T> type) throws MessageProcessingException;

    /**
     * Get the message entity, returns {@code null} if the message does not
     * contain an entity body.
     *
     * @param <T> entity type.
     * @param entityType the generic type of the entity.
     * @return the message entity or {@code null}.
     * @throws MessageProcessingException if the content of the message
     *     cannot be mapped to an entity of the requested type.
     * @since 2.0
     */
    public abstract <T> T getEntity(TypeLiteral<T> entityType) throws MessageProcessingException;

    /**
     * Check if there is an entity available in the response. The method returns
     * {@code true} if the entity is present, returns {@code false} otherwise.
     * <p/>
     * In case the response contained an entity, but it was already consumed as an
     * input stream via {@code getEntity(InputStream.class)}, the method returns
     * {@code false}.
     *
     * @return {@code true} if there is an entity present in the response, {@code false}
     *     otherwise.
     * @see #getEntity(java.lang.Class)
     * @since 2.0
     */
    public abstract boolean hasEntity();

    /**
     * Buffer the entity.
     * <p>
     * All the bytes of the original entity input stream will be read and stored
     * in memory. The original entity input stream will then be closed.
     *
     * @throws MessageProcessingException if there is an error processing the response.
     * @since 2.0
     */
    public abstract void bufferEntity() throws MessageProcessingException;

    /**
     * Close the response and all resources associated with the response.
     * As part of the operation, if open, the entity input stream is closed.
     *
     * @throws MessageProcessingException if there is an error closing the response.
     * @since 2.0
     */
    public abstract void close() throws MessageProcessingException;

    /**
     * Get metadata associated with the response as a map. The returned map
     * may be subsequently modified by the JAX-RS runtime. Values will be
     * serialized using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate}
     * if one is available via
     * {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     * for the class of the value or using the values {@code toString} method if a
     * header delegate is not available.
     * <p/>
     * This method is effectively a shortcut for
     * {@link #getHeaders()}.{@link ResponseHeaders#asMap() asMap()}.
     *
     * @return response metadata as a map
     */
    public abstract MultivaluedMap<String, Object> getMetadata();

    /**
     * Create a new ResponseBuilder by performing a shallow copy of an
     * existing Response. The returned builder has its own metadata map but
     * entries are simply references to the keys and values contained in the
     * supplied Response metadata map.
     *
     * @param response a Response from which the status code, entity and metadata
     * will be copied
     * @return a new ReponseBuilder
     */
    public static ResponseBuilder fromResponse(Response response) {
        ResponseBuilder b = status(response.getStatus());
        b.entity(response.getEntity());
        for (String headerName : response.getMetadata().keySet()) {
            List<Object> headerValues = response.getMetadata().get(headerName);
            for (Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status
     * @return a new ResponseBuilder
     * @throws IllegalArgumentException if status is null
     */
    public static ResponseBuilder status(StatusType status) {
        ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status
     * @return a new ResponseBuilder
     * @throws IllegalArgumentException if status is null
     */
    public static ResponseBuilder status(Status status) {
        return status((StatusType) status);
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status
     * @return a new ResponseBuilder
     * @throws IllegalArgumentException if status is less than 100 or greater
     * than 599.
     */
    public static ResponseBuilder status(int status) {
        ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }

    /**
     * Create a new ResponseBuilder with an OK status.
     *
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok() {
        ResponseBuilder b = status(Status.OK);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity) {
        ResponseBuilder b = ok();
        b.entity(entity);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data
     * @param type the media type of the entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, MediaType type) {
        ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data
     * @param type the media type of the entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, String type) {
        ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data
     * @param variant representation metadata
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, Variant variant) {
        ResponseBuilder b = ok();
        b.entity(entity);
        b.variant(variant);
        return b;
    }

    /**
     * Create a new ResponseBuilder with an server error status.
     *
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder serverError() {
        ResponseBuilder b = status(Status.INTERNAL_SERVER_ERROR);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a created resource, set the location
     * header using the supplied value.
     *
     * @param location the URI of the new resource. If a relative URI is
     * supplied it will be converted into an absolute URI by resolving it
     * relative to the request URI (see {@link UriInfo#getRequestUri}).
     * @return a new ResponseBuilder
     * @throws java.lang.IllegalArgumentException if location is null
     */
    public static ResponseBuilder created(URI location) {
        ResponseBuilder b = status(Status.CREATED).location(location);
        return b;
    }

    /**
     * Create a new ResponseBuilder for an empty response.
     *
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder noContent() {
        ResponseBuilder b = status(Status.NO_CONTENT);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     *
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notModified() {
        ResponseBuilder b = status(Status.NOT_MODIFIED);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     *
     * @param tag a tag for the unmodified entity
     * @return a new ResponseBuilder
     * @throws java.lang.IllegalArgumentException if tag is null
     */
    public static ResponseBuilder notModified(EntityTag tag) {
        ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status
     * and a strong entity tag. This is a shortcut
     * for <code>notModified(new EntityTag(<i>value</i>))</code>.
     *
     * @param tag the string content of a strong entity tag. The JAX-RS
     * runtime will quote the supplied value when creating the header.
     * @return a new ResponseBuilder
     * @throws java.lang.IllegalArgumentException if tag is null
     */
    public static ResponseBuilder notModified(String tag) {
        ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a redirection. Used in the
     * redirect-after-POST (aka POST/redirect/GET) pattern.
     *
     * @param location the redirection URI. If a relative URI is
     * supplied it will be converted into an absolute URI by resolving it
     * relative to the base URI of the application (see
     * {@link UriInfo#getBaseUri}).
     * @return a new ResponseBuilder
     * @throws java.lang.IllegalArgumentException if location is null
     */
    public static ResponseBuilder seeOther(URI location) {
        ResponseBuilder b = status(Status.SEE_OTHER).location(location);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a temporary redirection.
     *
     * @param location the redirection URI. If a relative URI is
     * supplied it will be converted into an absolute URI by resolving it
     * relative to the base URI of the application (see
     * {@link UriInfo#getBaseUri}).
     * @return a new ResponseBuilder
     * @throws java.lang.IllegalArgumentException if location is null
     */
    public static ResponseBuilder temporaryRedirect(URI location) {
        ResponseBuilder b = status(Status.TEMPORARY_REDIRECT).location(location);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a not acceptable response.
     *
     * @param variants list of variants that were available, a null value is
     * equivalent to an empty list.
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notAcceptable(List<Variant> variants) {
        ResponseBuilder b = status(Status.NOT_ACCEPTABLE).variants(variants);
        return b;
    }

    /**
     * A class used to build Response instances that contain metadata instead
     * of or in addition to an entity. An initial instance may be obtained via
     * static methods of the Response class, instance methods provide the
     * ability to set metadata. E.g. to create a response that indicates the
     * creation of a new resource:
     * <pre>&#64;POST
     * Response addWidget(...) {
     *   Widget w = ...
     *   URI widgetId = UriBuilder.fromResource(Widget.class)...
     *   return Response.created(widgetId).build();
     * }</pre>
     *
     * <p>Several methods have parameters of type URI, {@link UriBuilder} provides
     * convenient methods to create such values as does <code>URI.create()</code>.</p>
     *
     * <p>Where multiple variants of the same method are provided, the type of
     * the supplied parameter is retained in the metadata of the built
     * {@code Response}.</p>
     *
     */
    public static abstract class ResponseBuilder {

        /**
         * Protected constructor, use one of the static methods of
         * <code>Response</code> to obtain an instance.
         */
        protected ResponseBuilder() {
        }

        /**
         * Create a new builder instance.
         *
         * @return a new ResponseBuilder
         */
        protected static ResponseBuilder newInstance() {
            ResponseBuilder b = RuntimeDelegate.getInstance().createResponseBuilder();
            return b;
        }

        /**
         * Create a Response instance from the current ResponseBuilder. The builder
         * is reset to a blank state equivalent to calling the ok method.
         *
         * @return a Response instance
         */
        public abstract Response build();

        /**
         * Create a copy of the ResponseBuilder preserving its state.
         *
         * @return a copy of the ResponseBuilder
         */
        @Override
        public abstract ResponseBuilder clone();

        /**
         * Get the map of response properties.
         * <p>
         * A response property is an application-defined property that may be
         * added by the user, a filter, or the handler that is managing the
         * connection.
         *
         * @return the map of response properties.
         * @since 2.0
         */
        public abstract Map<String, Object> getProperties();

        /**
         * Get the status code associated with the response.
         *
         * @return the response status code or -1 if the status was not set.
         * @since 2.0
         */
        public abstract int getStatus();

        /**
         * Get the response status represented as a response {@link Status} enumeration
         * value.
         *
         * @return the status type instance, or {@code null} if there is no
         * mapping between the integer status code and the
         * {@link javax.ws.rs.core.Response.Status response status enumeration} value.
         * @since 2.0
         */
        public abstract Status getStatusEnum();

        /**
         * Get the response message headers. This method never returns {@code null}.
         *
         * @return response message headers. Returned headers may be empty but never
         *     {@code null}.
         * @see javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate
         *
         * @since 2.0
         */
        public abstract ResponseHeaders getHeaders();

        /**
         * Get the message entity, returns {@code null} if the message does not
         * contain an entity body.
         * <p/>
         * Upon sending, the response will be serialized using a {@link javax.ws.rs.ext.MessageBodyWriter}
         * for either the class of the entity or, in the case of {@link GenericEntity},
         * the value of {@link GenericEntity#getRawType()}.
         *
         * @return the message entity or {@code null} if there is no entity.
         * @see javax.ws.rs.ext.MessageBodyWriter
         * @since 2.0
         */
        public abstract Object getEntity();

        /**
         * Get the message entity, returns {@code null} if the message does not
         * contain an entity body.
         *
         * @param <T> entity type.
         * @param type the type of entity.
         * @return the message entity or {@code null}.
         * @throws MessageProcessingException if the content of the message
         *     cannot be mapped to an entity of the requested type.
         * @since 2.0
         */
        public abstract <T> T getEntity(Class<T> type) throws MessageProcessingException;

        /**
         * Get the message entity, returns {@code null} if the message does not
         * contain an entity body.
         *
         * @param <T> entity type.
         * @param entityType the generic type of the entity.
         * @return the message entity or {@code null}.
         * @throws MessageProcessingException if the content of the message
         *     cannot be mapped to an entity of the requested type.
         * @since 2.0
         */
        public abstract <T> T getEntity(TypeLiteral<T> entityType) throws MessageProcessingException;

        /**
         * Check if there is a message entity available.
         *
         * @return {@code true} if there is a message entity present.
         * @since 2.0
         */
        public abstract boolean hasEntity();

        /**
         * Set the status on the ResponseBuilder.
         *
         * @param status the response status
         * @return the updated ResponseBuilder
         * @throws IllegalArgumentException if status is less than 100 or greater
         * than 599.
         */
        public abstract ResponseBuilder status(int status);

        /**
         * Set the status on the ResponseBuilder.
         *
         * @param status the response status
         * @return the updated ResponseBuilder
         * @throws IllegalArgumentException if status is null
         */
        public ResponseBuilder status(StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            return status(status.getStatusCode());
        }

        /**
         * Set the status on the ResponseBuilder.
         *
         * @param status the response status
         * @return the updated ResponseBuilder
         * @throws IllegalArgumentException if status is null
         */
        public ResponseBuilder status(Status status) {
            return status((StatusType) status);
        }

        /**
         * Set the response entity in the builder.
         * <p />
         * Any Java type instance for a response entity, that is supported by the
         * runtime can be passed. It is the callers responsibility to wrap the
         * actual entity with {@link GenericEntity} if preservation of its generic
         * type is required. Note that the entity can be also set as an
         * {@link java.io.InputStream input stream}.
         * <p />
         * A specific entity media type can be set using one of the {@code type(...)}
         * methods.
         *
         * @param entity the request entity.
         * @return updated response builder instance.
         *
         * @see #type(javax.ws.rs.core.MediaType)
         * @see #type(java.lang.String)
         */
        public abstract ResponseBuilder entity(Object entity);

        // Headers
        // General headers
        /**
         * Set the list of allowed methods for the resource. Any duplicate method
         * names will be truncated to a single entry.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *     if {@code null} any existing allowed method list will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder allow(String... methods);

        /**
         * Set the list of allowed methods for the resource.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *     if {@code null} any existing allowed method list will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder allow(Set<String> methods);

        /**
         * Set the cache control data of the message.
         *
         * @param cacheControl the cache control directives, if {@code null}
         *     any existing cache control directives will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder cacheControl(CacheControl cacheControl);

        /**
         * Set the message entity content encoding.
         *
         * @param encoding the content encoding of the message entity,
         *     if {@code null} any existing value for content encoding will be
         *     removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder encoding(String encoding);

        /**
         * Add an arbitrary header.
         *
         * @param name the name of the header
         * @param value the value of the header, the header will be serialized
         *     using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if
         *     one is available via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
         *     for the class of {@code value} or using its {@code toString} method
         *     if a header delegate is not available. If {@code value} is {@code null}
         *     then all current headers of the same name will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder header(String name, Object value);

        /**
         * Replaces all existing headers with the newly supplied headers.
         *
         * @param headers new headers to be set, if {@code null} all existing
         *     headers will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder replaceAll(ResponseHeaders headers);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *     existing value for language will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder language(String language);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *     existing value for type will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder language(Locale language);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *     existing value for type will be removed
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder type(MediaType type);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *     existing value for type will be removed
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder type(String type);

        /**
         * Set message entity representation metadata.
         * <p/>
         * Equivalent to setting the values of content type, content language,
         * and content encoding separately using the values of the variant properties.
         *
         * @param variant metadata of the message entity, a {@code null} value is
         *     equivalent to a variant with all {@code null} properties.
         * @return the updated response builder.
         * @since 2.0
         *
         * @see #encoding(java.lang.String)
         * @see #language(java.util.Locale)
         * @see #type(javax.ws.rs.core.MediaType)
         */
        public abstract ResponseBuilder variant(Variant variant);

        // Response-specific headers
        /**
         * Set the content location.
         *
         * @param location the content location. Relative or absolute URIs
         *     may be used for the value of content location. If {@code null} any
         *     existing value for content location will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder contentLocation(URI location);

        /**
         * Add cookies to the response message.
         *
         * @param cookies new cookies that will accompany the response. A {@code null}
         *     value will remove all cookies, including those added via the
         *     {@link #header(java.lang.String, java.lang.Object)} method.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder cookie(NewCookie... cookies);

        /**
         * Set the response expiration date.
         *
         * @param expires the expiration date, if {@code null} removes any existing
         *     expires value.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder expires(Date expires);

        /**
         * Set the response entity last modification date.
         *
         * @param lastModified the last modified date, if {@code null} any existing
         *     last modified value will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder lastModified(Date lastModified);

        /**
         * Set the location.
         *
         * @param location the location. If a relative URI is supplied it will be
         *     converted into an absolute URI by resolving it relative to the
         *     base URI of the application (see {@link UriInfo#getBaseUri}).
         *     If {@code null} any existing value for location will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder location(URI location);

        /**
         * Set a response entity tag.
         *
         * @param tag the entity tag, if {@code null} any existing entity tag
         *     value will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder tag(EntityTag tag);

        /**
         * Set a strong response entity tag.
         * <p/>
         * This is a shortcut for <code>tag(new EntityTag(<i>value</i>))</code>.
         *
         * @param tag the string content of a strong entity tag. The JAX-RS
         *     runtime will quote the supplied value when creating the header.
         *     If {@code null} any existing entity tag value will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder tag(String tag);

        /**
         * Add a Vary header that lists the available variants.
         *
         * @param variants a list of available representation variants, a {@code null}
         *     value will remove an existing value for Vary header.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder variants(Variant... variants);

        /**
         * Add a Vary header that lists the available variants.
         *
         * @param variants a list of available representation variants, a {@code null}
         *     value will remove an existing value for Vary header.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder variants(List<Variant> variants);

        /**
         * Add one or more link headers.
         *
         * @param links links to be added to the message as headers, a {@code null}
         *     value will remove any existing Link headers.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder links(Link... links);

        /**
         * Add a link header.
         *
         * @param uri underlying URI for link header.
         * @param rel value of "rel" parameter.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder link(URI uri, String rel);

        /**
         * Add a link header.
         *
         * @param uri underlying URI for link header.
         * @param rel value of "rel" parameter.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder link(String uri, String rel);
    }

    /**
     * Base interface for statuses used in responses.
     *
     * @since 1.1
     */
    public interface StatusType {

        /**
         * Get the associated status code
         * @return the status code
         */
        public int getStatusCode();

        /**
         * Get the class of status code
         * @return the class of status code
         */
        public Status.Family getFamily();

        /**
         * Get the reason phrase
         * @return the reason phrase
         */
        public String getReasonPhrase();
    }

    /**
     * Commonly used status codes defined by HTTP, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
     * for the complete list. Additional status codes can be added by applications
     * by creating an implementation of {@link StatusType}.
     */
    public enum Status implements StatusType {

        /**
         * 200 OK, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>}.
         */
        OK(200, "OK"),
        /**
         * 201 Created, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1 documentation</a>}.
         */
        CREATED(201, "Created"),
        /**
         * 202 Accepted, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1 documentation</a>}.
         */
        ACCEPTED(202, "Accepted"),
        /**
         * 204 No Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1 documentation</a>}.
         */
        NO_CONTENT(204, "No Content"),
        /**
         * 205 Reset Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.6">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        RESET_CONTENT(205, "Reset Content"),
        /**
         * 206 Reset Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        PARTIAL_CONTENT(206, "Partial Content"),
        /**
         * 301 Moved Permanently, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1 documentation</a>}.
         */
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        /**
         * 302 Found, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.3">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        FOUND(302, "Found"),
        /**
         * 303 See Other, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1 documentation</a>}.
         */
        SEE_OTHER(303, "See Other"),
        /**
         * 304 Not Modified, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1 documentation</a>}.
         */
        NOT_MODIFIED(304, "Not Modified"),
        /**
         * 305 Use Proxy, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.6">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        USE_PROXY(305, "Use Proxy"),
        /**
         * 307 Temporary Redirect, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1 documentation</a>}.
         */
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),
        /**
         * 400 Bad Request, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1 documentation</a>}.
         */
        BAD_REQUEST(400, "Bad Request"),
        /**
         * 401 Unauthorized, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1 documentation</a>}.
         */
        UNAUTHORIZED(401, "Unauthorized"),
        /**
         * 402 Payment Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.3">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        PAYMENT_REQUIRED(402, "Payment Required"),
        /**
         * 403 Forbidden, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1 documentation</a>}.
         */
        FORBIDDEN(403, "Forbidden"),
        /**
         * 404 Not Found, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1 documentation</a>}.
         */
        NOT_FOUND(404, "Not Found"),
        /**
         * 405 Method Not Allowed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        /**
         * 406 Not Acceptable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1 documentation</a>}.
         */
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        /**
         * 407 Proxy Authentication Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.8">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        /**
         * 408 Request Timeout, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.9">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        REQUEST_TIMEOUT(408, "Request Timeout"),
        /**
         * 409 Conflict, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1 documentation</a>}.
         */
        CONFLICT(409, "Conflict"),
        /**
         * 410 Gone, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>}.
         */
        GONE(410, "Gone"),
        /**
         * 411 Length Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.12">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        LENGTH_REQUIRED(411, "Length Required"),
        /**
         * 412 Precondition Failed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1 documentation</a>}.
         */
        PRECONDITION_FAILED(412, "Precondition Failed"),
        /**
         * 413 Request Entity Too Large, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.14">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
        /**
         * 414 Request-URI Too Long, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.15">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
        /**
         * 415 Unsupported Media Type, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1 documentation</a>}.
         */
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        /**
         * 416 Requested Range Not Satisfiable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        REQUESTED_RANGE_NOT_SATIFIABLE(416, "Requested Range Not Satisfiable"),
        /**
         * 417 Expectation Failed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.18">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        EXPECTATION_FAILED(417, "Expectation Failed"),
        /**
         * 500 Internal Server Error, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1 documentation</a>}.
         */
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        /**
         * 501 Not Implemented, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        NOT_IMPLEMENTED(501, "Not Implemented"),
        /**
         * 502 Bad Gateway, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        BAD_GATEWAY(502, "Bad Gateway"),
        /**
         * 503 Service Unavailable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1 documentation</a>}.
         */
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        /**
         * 504 Gateway Timeout, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        /**
         * 505 HTTP Version Not Supported, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.6">HTTP/1.1 documentation</a>}.
         *
         * @since 2.0
         */
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
        private final int code;
        private final String reason;
        private Family family;

        /**
         * An enumeration representing the class of status code. Family is used
         * here since class is overloaded in Java.
         */
        public enum Family {

            INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, OTHER;

            /**
             * Get the response status family for the status code.
             *
             * @param statusCode response status code to get the family for.
             * @return family of the response status code.
             */
            public static Family familyOf(final int statusCode) {
                switch (statusCode / 100) {
                    case 1:
                        return Family.INFORMATIONAL;
                    case 2:
                        return Family.SUCCESSFUL;
                    case 3:
                        return Family.REDIRECTION;
                    case 4:
                        return Family.CLIENT_ERROR;
                    case 5:
                        return Family.SERVER_ERROR;
                    default:
                        return Family.OTHER;
                }
            }
        }

        Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = Family.familyOf(statusCode);
        }

        /**
         * Get the class of status code
         * @return the class of status code
         */
        @Override
        public Family getFamily() {
            return family;
        }

        /**
         * Get the associated status code
         * @return the status code
         */
        @Override
        public int getStatusCode() {
            return code;
        }

        /**
         * Get the reason phrase
         * @return the reason phrase
         */
        @Override
        public String getReasonPhrase() {
            return toString();
        }

        /**
         * Get the reason phrase
         * @return the reason phrase
         */
        @Override
        public String toString() {
            return reason;
        }

        /**
         * Convert a numerical status code into the corresponding Status
         * @param statusCode the numerical status code
         * @return the matching Status or null is no matching Status is defined
         */
        public static Status fromStatusCode(final int statusCode) {
            for (Status s : Status.values()) {
                if (s.code == statusCode) {
                    return s;
                }
            }
            return null;
        }
    }
}
