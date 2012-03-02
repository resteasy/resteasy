/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.FilterContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * An injectable helper for request processing, all methods throw an
 * {@link java.lang.IllegalStateException} if called outside the scope of a request
 * (e.g. from a provider constructor).
 *
 * Precondition processing (see the <code>evaluatePreconditions</code> methods)
 * can result in either a <code>null</code> return value to indicate that
 * preconditions have been met and that the request should continue, or
 * a non-null return value to indicate that preconditions were not met. In the
 * event that preconditions were not met, the returned <code>ResponseBuilder</code>
 * instance will have an appropriate status and will also include a <code>Vary</code>
 * header if the {@link #selectVariant(List)} method was called prior to to calling
 * <code>evaluatePreconditions</code>. It is the responsibility of the caller
 * to check the status and add additional metadata if required. E.g., see
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1, section 10.3.5</a>
 * for details of the headers that are expected to accompany a <code>304 Not Modified</code>
 * response.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @author Marek Potociar
 * @see Request.RequestBuilder
 * @since 1.0
 */
public interface Request {

    /**
     * An interface used to build {@link Request} instances, typically used in
     * JAX-RS filters. An initial instance may be obtained via {@link FilterContext}
     * that is passed to the filters.
     * <p/>
     * Methods of this interface provide the ability to set request metadata, such
     * as headers or entity.
     * <p/>
     * Where multiple variants of the same method are provided, the type of
     * the supplied parameter is retained in the metadata of the built {@code Request}.
     *
     * @since 2.0
     */
    public static interface RequestBuilder extends Cloneable {

        // Headers
        // General headers
        /**
         * Set the list of allowed methods for the resource. Any duplicate method
         * names will be truncated to a single entry.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *     if {@code null} any existing allowed method list will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder allow(String... methods);

        /**
         * Set the list of allowed methods for the resource.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *     if {@code null} any existing allowed method list will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder allow(Set<String> methods);

        /**
         * Set the cache control data of the message.
         *
         * @param cacheControl the cache control directives, if {@code null}
         *     any existing cache control directives will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder cacheControl(CacheControl cacheControl);

        /**
         * Set the message entity content encoding.
         *
         * @param encoding the content encoding of the message entity,
         *     if {@code null} any existing value for content encoding will be
         *     removed.
         * @return the updated request builder.
         */
        public RequestBuilder encoding(String encoding);

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
         * @return the updated request builder.
         */
        public RequestBuilder header(String name, Object value);

        /**
         * Replaces all existing headers with the newly supplied headers.
         *
         * @param headers new headers to be set, if {@code null} all existing
         *     headers will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder replaceAll(RequestHeaders headers);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *     existing value for language will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder language(String language);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *     existing value for type will be removed.
         * @return the updated request builder.
         */
        public RequestBuilder language(Locale language);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *     existing value for type will be removed
         * @return the updated request builder.
         */
        public RequestBuilder type(MediaType type);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *     existing value for type will be removed
         * @return the updated request builder.
         */
        public RequestBuilder type(String type);

        /**
         * Set message entity representation metadata.
         * <p/>
         * Equivalent to setting the values of content type, content language,
         * and content encoding separately using the values of the variant properties.
         *
         * @param variant metadata of the message entity, a {@code null} value is
         *     equivalent to a variant with all {@code null} properties.
         * @return the updated request builder.
         *
         * @see #encoding(java.lang.String)
         * @see #language(java.util.Locale)
         * @see #type(javax.ws.rs.core.MediaType)
         */
        public RequestBuilder variant(Variant variant);

        // Request-specific headers
        /**
         * Add acceptable media types.
         *
         * @param types an array of the acceptable media types
         * @return updated request builder.
         */
        public RequestBuilder accept(MediaType... types);

        /**
         * Add acceptable media types.
         *
         * @param types an array of the acceptable media types
         * @return updated request builder.
         */
        public RequestBuilder accept(String... types);

        /**
         * Add acceptable languages.
         *
         * @param locales an array of the acceptable languages
         * @return updated request builder.
         */
        public RequestBuilder acceptLanguage(Locale... locales);

        /**
         * Add acceptable languages.
         *
         * @param locales an array of the acceptable languages
         * @return updated request builder.
         */
        public RequestBuilder acceptLanguage(String... locales);

        /**
         * Add a cookie to be set.
         *
         * @param cookie to be set.
         * @return updated request builder.
         */
        public RequestBuilder cookie(Cookie cookie);

        // Request URI, entity....
        public RequestBuilder redirect(String uri);

        public RequestBuilder redirect(URI uri);

        public RequestBuilder redirect(UriBuilder uri);

        /**
         * Modify the HTTP method of the request.
         * <p />
         * The method name parameter can be any arbitrary, non-empty string, containing
         * but NOT limited to the command verbs of HTTP, WebDAV and other protocols.
         * An implementation MUST NOT expect the method to be part of any particular set
         * of methods. Any provided method name MUST be forwarded to the resource without
         * any limitations.
         *
         * @param httpMethod new method to be set on the request.
         * @return updated request builder instance.
         */
        public RequestBuilder method(String httpMethod);

        /**
         * Set the request entity in the builder.
         * <p />
         * Any Java type instance for a request entity, that is supported by the
         * runtime can be passed. It is the callers responsibility to wrap the
         * actual entity with {@link GenericEntity} if preservation of its generic
         * type is required. Note that the entity can be also set as an
         * {@link java.io.InputStream input stream}.
         * <p />
         * A specific entity media type can be set using one of the {@code type(...)}
         * methods.
         *
         * @param entity the request entity.
         * @return updated request builder instance.
         *
         * @see #entity(java.lang.Object, java.lang.annotation.Annotation[])
         * @see #type(javax.ws.rs.core.MediaType)
         * @see #type(java.lang.String)
         */
        public RequestBuilder entity(Object entity);

        /**
         * Set the request entity in the builder.
         * <p />
         * Any Java type instance for a request entity, that is supported by the
         * runtime can be passed. It is the callers responsibility to wrap the
         * actual entity with {@link GenericEntity} if preservation of its generic
         * type is required. Note that the entity can be also set as an
         * {@link java.io.InputStream input stream}.
         * <p />
         * A specific entity media type can be set using one of the {@code type(...)}
         * methods.
         *
         * @param entity the request entity.
         * @param annotations annotations that will be passed to the {@link MessageBodyWriter}.
         * @return updated request builder instance.
         *
         * @see #entity(java.lang.Object)
         * @see #type(javax.ws.rs.core.MediaType)
         * @see #type(java.lang.String)
         */
        public RequestBuilder entity(Object entity, Annotation[] annotations);

        /**
         * Create a copy of the request builder preserving its state.
         *
         * Note that the returned builder has its own {@link RequestHeaders request
         * headers} but the header values are shared with the original
         * {@code RequestBuilder} instance. Similarly, entity instance is also
         * shared with the original {@code RequestBuilder} instance.
         *
         * @return a copy of the request builder.
         */
        public RequestBuilder clone();

        public Request build();
    }

    /**
     * Get the request method, e.g. GET, POST, etc.
     *
     * @return the request method.
     * @see javax.ws.rs.HttpMethod
     */
    public String getMethod();

    /**
     * Get the request message headers. This method never returns {@code null}.
     *
     * @return request message headers. Returned headers may be empty but never
     *     {@code null}.
     * @see javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate
     * @since 2.0
     */
    public RequestHeaders getHeaders();

    /**
     * Get the absolute request URI. This includes query parameters and
     * any supplied fragment.
     *
     * @return the absolute request URI.
     * @since 2.0
     */
    public URI getUri();

    /**
     * Get the message entity Java instance. Returns {@code null} if the message
     * does not contain an entity body.
     *
     * @return the message entity or {@code null} if message does not contain an
     *     entity body.
     * @throws IllegalStateException in case the existing message entity is not
     *     {@link #isEntityRetrievable() retrievable} as a Java type. This is
     *     typically the case (on the server side) when the entity input stream
     *     has not been read as a Java type using one of the {@code readEntity(...)}
     *     methods yet.
     * @throws MessageProcessingException if the entity was previously fully consumed
     *     as an {@link InputStream input stream}.
     *
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #bufferEntity()
     * @see javax.ws.rs.ext.MessageBodyWriter
     * @since 2.0
     */
    public Object getEntity();

    /**
     * Read the message entity as an instance of specified Java type using
     * a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type. Returns {@code null} if
     * the message does not contain an entity body. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the consumed response entity stream (if open) and
     * makes the entity {@link #isEntityRetrievable() available for retrieval}.
     * <p />
     * A non-null message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}.
     * If the message has previously been read as an instance of a different Java type,
     * invoking this method will cause the cached entity instance to be serialized
     * into a temporary input stream using a compatible {@link javax.ws.rs.ext.MessageBodyWriter}
     * and then read again from the stream. This operation is thus potentially
     * expensive and should be used with care.
     * <p />
     * Note that a message entity can also be read as a raw entity
     * {@link java.io.InputStream input stream}, in which case it will be fully
     * consumed once the reading from the entity input stream is finished.
     * Once the entity is read as an input stream, any subsequent calls to
     * one of the {@code readEntity(...)} methods or {@link #getEntity()} method
     * on the same message instance will result in a {@link MessageProcessingException}
     * being thrown. It is up to the consumer of the entity input stream to ensure
     * that consuming the stream is properly mitigated (e.g. by substituting the
     * consumed response instance with a new one etc.).
     *
     * @param <T> entity instance Java type.
     * @param type the type of entity.
     * @return the message entity or {@code null} if message does not contain an
     *     entity body.
     * @throws MessageProcessingException if the content of the message cannot be
     *     mapped to an entity of the requested type or if the entity input stream
     *     was previously directly consumed by invoking {@code readEntity(InputStream.class)}.
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(java.lang.Class, java.lang.annotation.Annotation[])
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral, java.lang.annotation.Annotation[])
     * @see #bufferEntity()
     * @see #close()
     * @see javax.ws.rs.ext.MessageBodyWriter
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public <T> T readEntity(Class<T> type) throws MessageProcessingException;

    /**
     * Read the message entity as an instance of specified (generic) Java type using
     * a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type. Returns {@code null} if
     * the message does not contain an entity body. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the consumed response entity stream (if open) and
     * makes the entity {@link #isEntityRetrievable() available for retrieval}.
     * <p />
     * A non-null message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}.
     * If the message has previously been read as an instance of a different Java type,
     * invoking this method will cause the cached entity instance to be serialized
     * into a temporary input stream using a compatible {@link javax.ws.rs.ext.MessageBodyWriter}
     * and then read again from the stream. This operation is thus potentially
     * expensive and should be used with care.
     * <p />
     * Note that a message entity can also be read as a raw entity
     * {@link java.io.InputStream input stream}, in which case it will be fully
     * consumed once the reading from the entity input stream is finished.
     * Once the entity is read as an input stream, any subsequent calls to
     * one of the {@code readEntity(...)} methods or {@link #getEntity()} method
     * on the same message instance will result in a {@link MessageProcessingException}
     * being thrown. It is up to the consumer of the entity input stream to ensure
     * that consuming the stream is properly mitigated (e.g. by substituting the
     * consumed response instance with a new one etc.).
     *
     * @param <T> entity instance Java type.
     * @param entityType the type of entity; may be generic.
     * @return the message entity or {@code null} if message does not contain an
     *     entity body.
     * @throws MessageProcessingException if the content of the message cannot be
     *     mapped to an entity of the requested type or if the entity input stream
     *     was previously directly consumed by invoking {@code readEntity(InputStream.class)}.
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(javax.ws.rs.core.TypeLiteral, java.lang.annotation.Annotation[])
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(java.lang.Class, java.lang.annotation.Annotation[])
     * @see #bufferEntity()
     * @see #close()
     * @see javax.ws.rs.ext.MessageBodyWriter
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public <T> T readEntity(TypeLiteral<T> entityType) throws MessageProcessingException;

    /**
     * Read the message entity as an instance of specified Java type using
     * a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type. Returns {@code null} if
     * the message does not contain an entity body. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the consumed response entity stream (if open) and
     * makes the entity {@link #isEntityRetrievable() available for retrieval}.
     * <p />
     * A non-null message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}.
     * If the message has previously been read as an instance of a different Java type,
     * invoking this method will cause the cached entity instance to be serialized
     * into a temporary input stream using a compatible {@link javax.ws.rs.ext.MessageBodyWriter}
     * and then read again from the stream. This operation is thus potentially
     * expensive and should be used with care.
     * <p />
     * Note that a message entity can also be read as a raw entity
     * {@link java.io.InputStream input stream}, in which case it will be fully
     * consumed once the reading from the entity input stream is finished.
     * Once the entity is read as an input stream, any subsequent calls to
     * one of the {@code readEntity(...)} methods or {@link #getEntity()} method
     * on the same message instance will result in a {@link MessageProcessingException}
     * being thrown. It is up to the consumer of the entity input stream to ensure
     * that consuming the stream is properly mitigated (e.g. by substituting the
     * consumed response instance with a new one etc.).
     *
     * @param <T> entity instance Java type.
     * @param type the type of entity.
     * @param annotations annotations that will be passed to the {@link MessageBodyReader}.
     * @return the message entity or {@code null} if message does not contain an
     *     entity body.
     * @throws MessageProcessingException if the content of the message cannot be
     *     mapped to an entity of the requested type or if the entity input stream
     *     was previously directly consumed by invoking {@code readEntity(InputStream.class)}.
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral, java.lang.annotation.Annotation[])
     * @see #bufferEntity()
     * @see #close()
     * @see javax.ws.rs.ext.MessageBodyWriter
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public <T> T readEntity(Class<T> type, Annotation[] annotations) throws MessageProcessingException;

    /**
     * Read the message entity as an instance of specified (generic) Java type using
     * a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type. Returns {@code null} if
     * the message does not contain an entity body. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the consumed response entity stream (if open) and
     * makes the entity {@link #isEntityRetrievable() available for retrieval}.
     * <p />
     * A non-null message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}.
     * If the message has previously been read as an instance of a different Java type,
     * invoking this method will cause the cached entity instance to be serialized
     * into a temporary input stream using a compatible {@link javax.ws.rs.ext.MessageBodyWriter}
     * and then read again from the stream. This operation is thus potentially
     * expensive and should be used with care.
     * <p />
     * Note that a message entity can also be read as a raw entity
     * {@link java.io.InputStream input stream}, in which case it will be fully
     * consumed once the reading from the entity input stream is finished.
     * Once the entity is read as an input stream, any subsequent calls to
     * one of the {@code readEntity(...)} methods or {@link #getEntity()} method
     * on the same message instance will result in a {@link MessageProcessingException}
     * being thrown. It is up to the consumer of the entity input stream to ensure
     * that consuming the stream is properly mitigated (e.g. by substituting the
     * consumed response instance with a new one etc.).
     *
     * @param <T> entity instance Java type.
     * @param entityType the type of entity; may be generic.
     * @param annotations annotations that will be passed to the {@link MessageBodyReader}.
     * @return the message entity or {@code null} if message does not contain an
     *     entity body.
     * @throws MessageProcessingException if the content of the message cannot be
     *     mapped to an entity of the requested type or if the entity input stream
     *     was previously directly consumed by invoking {@code readEntity(InputStream.class)}.
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(java.lang.Class, java.lang.annotation.Annotation[])
     * @see #bufferEntity()
     * @see #close()
     * @see javax.ws.rs.ext.MessageBodyWriter
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public <T> T readEntity(TypeLiteral<T> entityType, Annotation[] annotations) throws MessageProcessingException;

    /**
     * Check if there is an entity available in the request. The method returns
     * {@code true} if the entity is present, returns {@code false} otherwise.
     * <p/>
     * In case the message contained an entity, but it was already consumed as
     * an input stream via {@code readEntity(InputStream.class)}, the method
     * returns {@code false}.
     *
     * @return {@code true} if there is an entity present in the message,
     *     {@code false} otherwise.
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @since 2.0
     */
    public boolean hasEntity();

    /**
     * Check if the entity instance is {@link #hasEntity() present} and available
     * for a retrieval via {@link #getEntity()}.
     *
     * The method returns {@code true} if the entity is retrievable, returns {@code false}
     * in case there is no entity associated with the message or if the existing
     * message entity is not available as a Java type. This is typically the case
     * (on the server side) when the entity input stream has not been consumed
     * using one of the {@code readEntity(...)} methods yet.
     * <p />
     * Note that even though {@link #bufferEntity() entity buffering} closes
     * the original entity input stream, the buffered entity may still not be
     * retrievable, unless the buffered data was previously read using one of the
     * {@code readEntity(...)} methods.
     *
     * @return {@code true} if there is a retrievable message entity instance
     *     present, {@code false} otherwise.
     * @see #hasEntity()
     * @see #getEntity()
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #bufferEntity()
     * @since 2.0
     */
    public abstract boolean isEntityRetrievable();

    /**
     * Buffer the message entity.
     *
     * In case the message entity input stream is open, all the bytes of the
     * original entity input stream are read and stored in memory. The original
     * entity input stream is automatically {@link #close() closed} as part of
     * the operation.
     * <p />
     * This operation is idempotent, i.e. it can be invoked multiple times with
     * the same effect which also means that calling the {@code bufferEntity()}
     * method on an already buffered (and thus closed) message instance is legal
     * and has no further effect.
     * <p />
     * Note that even though entity buffering closes the original entity input
     * stream, the buffered entity may still not be {@link #isEntityRetrievable()
     * retrievable}, unless the buffered data was previously read using one of
     * the {@code readEntity(...)} methods.
     *
     * @throws MessageProcessingException if there is an error buffering the
     *     message entity.
     * @see #hasEntity()
     * @see #isEntityRetrievable()
     * @see #getEntity()
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @see #close()
     * @since 2.0
     */
    public abstract void bufferEntity() throws MessageProcessingException;

    /**
     * Close the message entity input stream (if available and open).
     *
     * This operation is idempotent, i.e. it can be invoked multiple times with the
     * same effect which also means that calling the {@code close()} method on an
     * already closed message instance is legal and has no further effect.
     * <p/>
     * If the {@code close()} method is invoked before the message entity has been
     * fully read from the input stream, any subsequent attempt to read the entity
     * will result in an {@link MessageProcessingException} being thrown.
     * <p/>
     * Closing an instance that has already been consumed has no effect. Similarly,
     * closing an instance with no entity has not effect.
     *
     * @throws MessageProcessingException if there is an error closing the response.
     * @see #readEntity(java.lang.Class)
     * @see #readEntity(javax.ws.rs.core.TypeLiteral)
     * @since 2.0
     */
    public abstract void close() throws MessageProcessingException;

    /**
     * Get a mutable map of request-scoped properties that can be used for communication
     * between different request/response processing components. May be empty, but
     * MUST never be {@code null}. In the scope of a single request/response processing,
     * a same property map instance is shared by the following methods:
     * <ul>
     *     <li>{@link javax.ws.rs.core.Request#getProperties() }</li>
     *     <li>{@link javax.ws.rs.core.Response#getProperties() }</li>
     *     <li>{@link javax.ws.rs.ext.FilterContext#getProperties() }</li>
     *     <li>{@link javax.ws.rs.ext.InterceptorContext#getProperties() }</li>
     * </ul>
     * A request-scoped property is an application-defined property that may be
     * added, removed or modified by any of the components (user, filter, interceptor etc.)
     * that participate in a given request/response processing flow.
     * <p />
     * On the client side, this property map is initialized by calling
     * {@link javax.ws.rs.client.Configuration#setProperties(java.util.Map) } or
     * {@link javax.ws.rs.client.Configuration#setProperty(java.lang.String, java.lang.Object) }
     * on the configuration object associated with the corresponding
     * {@link javax.ws.rs.client.Invocation request invocation}.
     * <p />
     * On the server side, specifying the initial values is implementation-specific.
     * <p />
     * If there are no initial properties set, the request-scoped property map is
     * initialized to an empty map.
     *
     * @return a mutable request-scoped property map.
     * @see javax.ws.rs.client.Configuration
     *
     * @since 2.0
     */
    public Map<String, Object> getProperties();

    /**
     * Select the representation variant that best matches the request. More
     * explicit variants are chosen ahead of less explicit ones. A vary header
     * is computed from the supplied list and automatically added to the
     * response.
     *
     * @param variants a list of Variant that describe all of the
     * available representation variants.
     * @return the variant that best matches the request.
     * @see Variant.VariantListBuilder
     * @throws java.lang.IllegalArgumentException if variants is empty or null
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     */
    public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException;

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param eTag an ETag for the current state of the resource
     * @return null if the preconditions are met or a ResponseBuilder set with
     * the appropriate status if the preconditions are not met. A returned
     * ResponseBuilder will include an ETag header set with the value of eTag.
     * @throws java.lang.IllegalArgumentException if eTag is null
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     */
    public ResponseBuilder evaluatePreconditions(EntityTag eTag);

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param lastModified a date that specifies the modification date of the resource
     * @return null if the preconditions are met or a ResponseBuilder set with
     * the appropriate status if the preconditions are not met.
     * @throws java.lang.IllegalArgumentException if lastModified is null
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     */
    public ResponseBuilder evaluatePreconditions(Date lastModified);

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param lastModified a date that specifies the modification date of the resource
     * @param eTag an ETag for the current state of the resource
     * @return null if the preconditions are met or a ResponseBuilder set with
     * the appropriate status if the preconditions are not met.  A returned
     * ResponseBuilder will include an ETag header set with the value of eTag.
     * @throws java.lang.IllegalArgumentException if lastModified or eTag is null
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     */
    public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag);

    /**
     * Evaluate request preconditions for a resource that does not currently
     * exist. The primary use of this method is to support the {@link <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">
     * If-Match: *</a>} and {@link <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">
     * If-None-Match: *</a>} preconditions.
     *
     * <p>Note that both preconditions <code>If-None-Match: *</code> and
     * <code>If-None-Match: <i>something</i></code> will always be considered to
     * have been met and it is the applications responsibility
     * to enforce any additional method-specific semantics. E.g. a
     * <code>PUT</code> on a resource that does not exist might succeed whereas
     * a <code>GET</code> on a resource that does not exist would likely result
     * in a 404 response. It would be the responsibility of the application to
     * generate the 404 response.</p>
     *
     * @return null if the preconditions are met or a ResponseBuilder set with
     * the appropriate status if the preconditions are not met.
     * @throws java.lang.IllegalStateException if called outside the scope of
     * a request
     * @since 1.1
     */
    public ResponseBuilder evaluatePreconditions();
}
