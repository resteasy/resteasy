/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
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

import javax.ws.rs.ProcessingException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Defines the contract between a returned instance and the runtime when
 * an application needs to provide meta-data to the runtime.
 * <p>
 * An application class should not extend this class directly. {@code Response} class is
 * reserved for an extension by a JAX-RS implementation providers. An application should use one
 * of the static methods to create a {@code Response} instance using a ResponseBuilder.
 * </p>
 * <p>
 * Several methods have parameters of type URI, {@link UriBuilder} provides
 * convenient methods to create such values as does {@link URI#create(java.lang.String)}.
 * </p>
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
     * Get the status code associated with the response.
     *
     * @return the response status code.
     */
    public abstract int getStatus();

    /**
     * Get the complete status information associated with the response.
     *
     * @return the response status information. The returned value is never
     *         {@code null}.
     * @since 2.0
     */
    public abstract StatusType getStatusInfo();

    /**
     * Get the message entity Java instance. Returns {@code null} if the message
     * does not contain an entity body.
     * <p>
     * If the entity is represented by an un-consumed {@link InputStream input stream}
     * the method will return the input stream.
     * </p>
     *
     * @return the message entity or {@code null} if message does not contain an
     *         entity body (i.e. when {@link #hasEntity()} returns {@code false}).
     * @throws IllegalStateException if the entity was previously fully consumed
     *                               as an {@link InputStream input stream}, or
     *                               if the response has been {@link #close() closed}.
     */
    public abstract Object getEntity();

    /**
     * Read the message entity input stream as an instance of specified Java type
     * using a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type.
     * <p>
     * Method throws an {@link ProcessingException} if the content of the
     * message cannot be mapped to an entity of the requested type and
     * {@link IllegalStateException} in case the entity is not backed by an input
     * stream or if the original entity input stream has already been consumed
     * without {@link #bufferEntity() buffering} the entity data prior consuming.
     * </p>
     * <p>
     * A message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the an unconsumed original response entity data stream
     * if open. In case the entity data has been buffered, the buffer will be reset
     * prior consuming the buffered data to enable subsequent invocations of
     * {@code readEntity(...)} methods on this response.
     * </p>
     *
     * @param <T>        entity instance Java type.
     * @param entityType the type of entity.
     * @return the message entity; for a zero-length response entities returns a corresponding
     *         Java object that represents zero-length data. In case no zero-length representation
     *         is defined for the Java type, a {@link ProcessingException} wrapping the
     *         underlying {@link NoContentException} is thrown.
     * @throws ProcessingException   if the content of the message cannot be
     *                               mapped to an entity of the requested type.
     * @throws IllegalStateException if the entity is not backed by an input stream,
     *                               the response has been {@link #close() closed} already,
     *                               or if the entity input stream has been fully consumed already and has
     *                               not been buffered prior consuming.
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public abstract <T> T readEntity(Class<T> entityType);

    /**
     * Read the message entity input stream as an instance of specified Java type
     * using a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type.
     * <p>
     * Method throws an {@link ProcessingException} if the content of the
     * message cannot be mapped to an entity of the requested type and
     * {@link IllegalStateException} in case the entity is not backed by an input
     * stream or if the original entity input stream has already been consumed
     * without {@link #bufferEntity() buffering} the entity data prior consuming.
     * </p>
     * <p>
     * A message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the an unconsumed original response entity data stream
     * if open. In case the entity data has been buffered, the buffer will be reset
     * prior consuming the buffered data to enable subsequent invocations of
     * {@code readEntity(...)} methods on this response.
     * </p>
     *
     * @param <T>        entity instance Java type.
     * @param entityType the type of entity; may be generic.
     * @return the message entity; for a zero-length response entities returns a corresponding
     *         Java object that represents zero-length data. In case no zero-length representation
     *         is defined for the Java type, a {@link ProcessingException} wrapping the
     *         underlying {@link NoContentException} is thrown.
     * @throws ProcessingException   if the content of the message cannot be
     *                               mapped to an entity of the requested type.
     * @throws IllegalStateException if the entity is not backed by an input stream,
     *                               the response has been {@link #close() closed} already,
     *                               or if the entity input stream has been fully consumed already and has
     *                               not been buffered prior consuming.
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public abstract <T> T readEntity(GenericType<T> entityType);

    /**
     * Read the message entity input stream as an instance of specified Java type
     * using a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type.
     * <p>
     * Method throws an {@link ProcessingException} if the content of the
     * message cannot be mapped to an entity of the requested type and
     * {@link IllegalStateException} in case the entity is not backed by an input
     * stream or if the original entity input stream has already been consumed
     * without {@link #bufferEntity() buffering} the entity data prior consuming.
     * </p>
     * <p>
     * A message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the an unconsumed original response entity data stream
     * if open. In case the entity data has been buffered, the buffer will be reset
     * prior consuming the buffered data to enable subsequent invocations of
     * {@code readEntity(...)} methods on this response.
     * </p>
     *
     * @param <T>         entity instance Java type.
     * @param entityType  the type of entity.
     * @param annotations annotations that will be passed to the {@link MessageBodyReader}.
     * @return the message entity; for a zero-length response entities returns a corresponding
     *         Java object that represents zero-length data. In case no zero-length representation
     *         is defined for the Java type, a {@link ProcessingException} wrapping the
     *         underlying {@link NoContentException} is thrown.
     * @throws ProcessingException   if the content of the message cannot be
     *                               mapped to an entity of the requested type.
     * @throws IllegalStateException if the entity is not backed by an input stream,
     *                               the response has been {@link #close() closed} already,
     *                               or if the entity input stream has been fully consumed already and has
     *                               not been buffered prior consuming.
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public abstract <T> T readEntity(Class<T> entityType, Annotation[] annotations);

    /**
     * Read the message entity input stream as an instance of specified Java type
     * using a {@link javax.ws.rs.ext.MessageBodyReader} that supports mapping the
     * message entity stream onto the requested type.
     * <p>
     * Method throws an {@link ProcessingException} if the content of the
     * message cannot be mapped to an entity of the requested type and
     * {@link IllegalStateException} in case the entity is not backed by an input
     * stream or if the original entity input stream has already been consumed
     * without {@link #bufferEntity() buffering} the entity data prior consuming.
     * </p>
     * <p>
     * A message instance returned from this method will be cached for
     * subsequent retrievals via {@link #getEntity()}. Unless the supplied entity
     * type is an {@link java.io.InputStream input stream}, this method automatically
     * {@link #close() closes} the an unconsumed original response entity data stream
     * if open. In case the entity data has been buffered, the buffer will be reset
     * prior consuming the buffered data to enable subsequent invocations of
     * {@code readEntity(...)} methods on this response.
     * </p>
     *
     * @param <T>         entity instance Java type.
     * @param entityType  the type of entity; may be generic.
     * @param annotations annotations that will be passed to the {@link MessageBodyReader}.
     * @return the message entity; for a zero-length response entities returns a corresponding
     *         Java object that represents zero-length data. In case no zero-length representation
     *         is defined for the Java type, a {@link ProcessingException} wrapping the
     *         underlying {@link NoContentException} is thrown.
     * @throws ProcessingException   if the content of the message cannot be
     *                               mapped to an entity of the requested type.
     * @throws IllegalStateException if the entity is not backed by an input stream,
     *                               the response has been {@link #close() closed} already,
     *                               or if the entity input stream has been fully consumed already and has
     *                               not been buffered prior consuming.
     * @see javax.ws.rs.ext.MessageBodyReader
     * @since 2.0
     */
    public abstract <T> T readEntity(GenericType<T> entityType, Annotation[] annotations);

    /**
     * Check if there is an entity available in the response. The method returns
     * {@code true} if the entity is present, returns {@code false} otherwise.
     * <p>
     * Note that the method may return {@code true} also for response messages with
     * a zero-length content, in case the <tt>{@value javax.ws.rs.core.HttpHeaders#CONTENT_LENGTH}</tt> and
     * <tt>{@value javax.ws.rs.core.HttpHeaders#CONTENT_TYPE}</tt> headers are specified in the message.
     * In such case, an attempt to read the entity using one of the {@code readEntity(...)}
     * methods will return a corresponding instance representing a zero-length entity for a
     * given Java type or produce a {@link ProcessingException} in case no such instance
     * is available for the Java type.
     * </p>
     *
     * @return {@code true} if there is an entity present in the message,
     *         {@code false} otherwise.
     * @throws IllegalStateException in case the response has been {@link #close() closed}.
     * @since 2.0
     */
    public abstract boolean hasEntity();

    /**
     * Buffer the message entity data.
     * <p>
     * In case the message entity is backed by an unconsumed entity input stream,
     * all the bytes of the original entity input stream are read and stored in a
     * local buffer. The original entity input stream is consumed and automatically
     * closed as part of the operation and the method returns {@code true}.
     * </p>
     * <p>
     * In case the response entity instance is not backed by an unconsumed input stream
     * an invocation of {@code bufferEntity} method is ignored and the method returns
     * {@code false}.
     * </p>
     * <p>
     * This operation is idempotent, i.e. it can be invoked multiple times with
     * the same effect which also means that calling the {@code bufferEntity()}
     * method on an already buffered (and thus closed) message instance is legal
     * and has no further effect. Also, the result returned by the {@code bufferEntity()}
     * method is consistent across all invocations of the method on the same
     * {@code Response} instance.
     * </p>
     * <p>
     * Buffering the message entity data allows for multiple invocations of
     * {@code readEntity(...)} methods on the response instance. Note however, that
     * once the response instance itself is {@link #close() closed}, the implementations
     * are expected to release the buffered message entity data too. Therefore any subsequent
     * attempts to read a message entity stream on such closed response will result in an
     * {@link IllegalStateException} being thrown.
     * </p>
     *
     * @return {@code true} if the message entity input stream was available and
     *         was buffered successfully, returns {@code false} if the entity stream
     *         was not available.
     * @throws ProcessingException   if there was an error while buffering the entity
     *                               input stream.
     * @throws IllegalStateException in case the response has been {@link #close() closed}.
     * @since 2.0
     */
    public abstract boolean bufferEntity();

    /**
     * Close the underlying message entity input stream (if available and open)
     * as well as releases any other resources associated with the response
     * (e.g. {@link #bufferEntity() buffered message entity data}).
     * <p>
     * This operation is idempotent, i.e. it can be invoked multiple times with the
     * same effect which also means that calling the {@code close()} method on an
     * already closed message instance is legal and has no further effect.
     * </p>
     * <p>
     * The {@code close()} method should be invoked on all instances that
     * contain an un-consumed entity input stream to ensure the resources associated
     * with the instance are properly cleaned-up and prevent potential memory leaks.
     * This is typical for client-side scenarios where application layer code
     * processes only the response headers and ignores the response entity.
     * </p>
     * <p>
     * Any attempts to manipulate (read, get, buffer) a message entity on a closed response
     * will result in an {@link IllegalStateException} being thrown.
     * </p>
     *
     * @throws ProcessingException if there is an error closing the response.
     * @since 2.0
     */
    public abstract void close();

    /**
     * Get the media type of the message entity.
     *
     * @return the media type or {@code null} if there is no response entity.
     * @since 2.0
     */
    public abstract MediaType getMediaType();

    /**
     * Get the language of the message entity.
     *
     * @return the language of the entity or null if not specified.
     * @since 2.0
     */
    public abstract Locale getLanguage();

    /**
     * Get Content-Length value.
     *
     * @return Content-Length as integer if present and valid number. In other
     *         cases returns {@code -1}.
     * @since 2.0
     */
    public abstract int getLength();

    /**
     * Get the allowed HTTP methods from the Allow HTTP header.
     *
     * @return the allowed HTTP methods, all methods will returned as upper case
     *         strings.
     * @since 2.0
     */
    public abstract Set<String> getAllowedMethods();

    /**
     * Get any new cookies set on the response message.
     *
     * @return a read-only map of cookie name (String) to Cookie.
     * @since 2.0
     */
    public abstract Map<String, NewCookie> getCookies();

    /**
     * Get the entity tag.
     *
     * @return the entity tag, otherwise {@code null} if not present.
     * @since 2.0
     */
    public abstract EntityTag getEntityTag();

    /**
     * Get message date.
     *
     * @return the message date, otherwise {@code null} if not present.
     * @since 2.0
     */
    public abstract Date getDate();

    /**
     * Get the last modified date.
     *
     * @return the last modified date, otherwise {@code null} if not present.
     * @since 2.0
     */
    public abstract Date getLastModified();

    /**
     * Get the location.
     *
     * @return the location URI, otherwise {@code null} if not present.
     * @since 2.0
     */
    public abstract URI getLocation();

    /**
     * Get the links attached to the message as headers. Any links in the message
     * that are relative must be resolved with respect to the actual request URI
     * that produced this response. Note that request URIs may be updated by
     * filters, so the actual request URI may differ from that in the original
     * invocation.
     *
     * @return links, may return empty {@link Set} if no links are present. Does
     *         not return {@code null}.
     * @since 2.0
     */
    public abstract Set<Link> getLinks();

    /**
     * Check if link for relation exists.
     *
     * @param relation link relation.
     * @return {@code true} if the link for the relation is present in the
     *         {@link #getHeaders() message headers}, {@code false} otherwise.
     * @since 2.0
     */
    public abstract boolean hasLink(String relation);

    /**
     * Get the link for the relation. A relative link is resolved with respect
     * to the actual request URI that produced this response. Note that request
     * URIs may be updated by filters, so the actual request URI may differ from
     * that in the original invocation.
     *
     * @param relation link relation.
     * @return the link for the relation, otherwise {@code null} if not present.
     * @since 2.0
     */
    public abstract Link getLink(String relation);

    /**
     * Convenience method that returns a {@link Link.Builder} for the relation.
     * See {@link #getLink} for more information.
     *
     * @param relation link relation.
     * @return the link builder for the relation, otherwise {@code null} if not
     *         present.
     * @since 2.0
     */
    public abstract Link.Builder getLinkBuilder(String relation);

    /**
     * See {@link #getHeaders()}.
     *
     * This method is considered deprecated. Users are encouraged to switch their
     * code to use the {@code getHeaders()} method instead. The method may be annotated
     * as {@link Deprecated &#64;Deprecated} in a future release of JAX-RS API.
     *
     * @return response headers as a multivalued map.
     */
    public abstract MultivaluedMap<String, Object> getMetadata();

    /**
     * Get view of the response headers and their object values.
     *
     * The underlying header data may be subsequently modified by the JAX-RS runtime on the
     * server side. Changes in the underlying header data are reflected in this view.
     * <p>
     * On the server-side, when the message is sent, the non-string values will be serialized
     * using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if one is available via
     * {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)} for the
     * class of the value or using the values {@code toString} method if a header delegate is
     * not available.
     * </p>
     * <p>
     * On the client side, the returned map is identical to the one returned by
     * {@link #getStringHeaders()}.
     * </p>
     *
     * @return response headers as an object view of header values.
     * @see #getStringHeaders()
     * @see #getHeaderString
     * @since 2.0
     */
    public MultivaluedMap<String, Object> getHeaders() {
        return getMetadata();
    }

    /**
     * Get view of the response headers and their string values.
     *
     * The underlying header data may be subsequently modified by the JAX-RS runtime on
     * the server side. Changes in the underlying header data are reflected in this view.
     *
     * @return response headers as a string view of header values.
     * @see #getHeaders()
     * @see #getHeaderString
     * @since 2.0
     */
    public abstract MultivaluedMap<String, String> getStringHeaders();

    /**
     * Get a message header as a single string value.
     *
     * Each single header value is converted to String using a
     * {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if one is available
     * via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     * for the header value class or using its {@code toString} method  if a header
     * delegate is not available.
     *
     * @param name the message header.
     * @return the message header value. If the message header is not present then
     *         {@code null} is returned. If the message header is present but has no
     *         value then the empty string is returned. If the message header is present
     *         more than once then the values of joined together and separated by a ','
     *         character.
     * @see #getHeaders()
     * @see #getStringHeaders()
     * @since 2.0
     */
    public abstract String getHeaderString(String name);

    /**
     * Create a new ResponseBuilder by performing a shallow copy of an
     * existing Response.
     * <p>
     * The returned builder has its own {@link #getHeaders() response headers}
     * but the header values are shared with the original {@code Response} instance.
     * The original response entity instance reference is set in the new response
     * builder.
     * </p>
     * <p>
     * Note that if the entity is backed by an un-consumed input stream, the
     * reference to the stream is copied. In such case make sure to
     * {@link #bufferEntity() buffer} the entity stream of the original response
     * instance before passing it to this method.
     * </p>
     *
     * @param response a Response from which the status code, entity and
     *                 {@link #getHeaders() response headers} will be copied.
     * @return a new response builder.
     * @since 2.0
     */
    public static ResponseBuilder fromResponse(Response response) {
        ResponseBuilder b = status(response.getStatus());
        if (response.hasEntity()) {
            b.entity(response.getEntity());
        }
        for (String headerName : response.getHeaders().keySet()) {
            List<Object> headerValues = response.getHeaders().get(headerName);
            for (Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status.
     * @return a new response builder.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public static ResponseBuilder status(StatusType status) {
        return ResponseBuilder.newInstance().status(status);
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status.
     * @return a new response builder.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public static ResponseBuilder status(Status status) {
        return status((StatusType) status);
    }

    /**
     * Create a new ResponseBuilder with the supplied status.
     *
     * @param status the response status.
     * @return a new response builder.
     * @throws IllegalArgumentException if status is less than {@code 100} or greater
     *                                  than {@code 599}.
     */
    public static ResponseBuilder status(int status) {
        return ResponseBuilder.newInstance().status(status);
    }

    /**
     * Create a new ResponseBuilder with an OK status.
     *
     * @return a new response builder.
     */
    public static ResponseBuilder ok() {
        return status(Status.OK);
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data.
     * @return a new response builder.
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
     * @param entity the representation entity data.
     * @param type   the media type of the entity.
     * @return a new response builder.
     */
    public static ResponseBuilder ok(Object entity, MediaType type) {
        return ok().entity(entity).type(type);
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data.
     * @param type   the media type of the entity.
     * @return a new response builder.
     */
    public static ResponseBuilder ok(Object entity, String type) {
        return ok().entity(entity).type(type);
    }

    /**
     * Create a new ResponseBuilder that contains a representation. It is the
     * callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity  the representation entity data.
     * @param variant representation metadata.
     * @return a new response builder.
     */
    public static ResponseBuilder ok(Object entity, Variant variant) {
        return ok().entity(entity).variant(variant);
    }

    /**
     * Create a new ResponseBuilder with an server error status.
     *
     * @return a new response builder.
     */
    public static ResponseBuilder serverError() {
        return status(Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create a new ResponseBuilder for a created resource, set the location
     * header using the supplied value.
     *
     * @param location the URI of the new resource. If a relative URI is
     *                 supplied it will be converted into an absolute URI by resolving it
     *                 relative to the request URI (see {@link UriInfo#getRequestUri}).
     * @return a new response builder.
     * @throws java.lang.IllegalArgumentException
     *          if location is {@code null}.
     */
    public static ResponseBuilder created(URI location) {
        return status(Status.CREATED).location(location);
    }

    /**
     * Create a new ResponseBuilder with an ACCEPTED status.
     *
     * @return a new response builder.
     * @since 2.0
     */
    public static ResponseBuilder accepted() {
        return status(Status.ACCEPTED);
    }

    /**
     * Create a new ResponseBuilder with an ACCEPTED status that contains
     * a representation. It is the callers responsibility to wrap the actual entity with
     * {@link GenericEntity} if preservation of its generic type is required.
     *
     * @param entity the representation entity data.
     * @return a new response builder.
     * @since 2.0
     */
    public static ResponseBuilder accepted(Object entity) {
        return accepted().entity(entity);
    }

    /**
     * Create a new ResponseBuilder for an empty response.
     *
     * @return a new response builder.
     */
    public static ResponseBuilder noContent() {
        return status(Status.NO_CONTENT);
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     *
     * @return a new response builder.
     */
    public static ResponseBuilder notModified() {
        return status(Status.NOT_MODIFIED);
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     *
     * @param tag a tag for the unmodified entity.
     * @return a new response builder.
     * @throws java.lang.IllegalArgumentException
     *          if tag is {@code null}.
     */
    public static ResponseBuilder notModified(EntityTag tag) {
        return notModified().tag(tag);
    }

    /**
     * Create a new ResponseBuilder with a not-modified status
     * and a strong entity tag. This is a shortcut
     * for <code>notModified(new EntityTag(<i>value</i>))</code>.
     *
     * @param tag the string content of a strong entity tag. The JAX-RS
     *            runtime will quote the supplied value when creating the
     *            header.
     * @return a new response builder.
     * @throws IllegalArgumentException if tag is {@code null}.
     */
    @SuppressWarnings("HtmlTagCanBeJavadocTag")
    public static ResponseBuilder notModified(String tag) {
        return notModified().tag(tag);
    }

    /**
     * Create a new ResponseBuilder for a redirection. Used in the
     * redirect-after-POST (aka POST/redirect/GET) pattern.
     *
     * @param location the redirection URI. If a relative URI is
     *                 supplied it will be converted into an absolute URI by resolving it
     *                 relative to the base URI of the application (see
     *                 {@link UriInfo#getBaseUri}).
     * @return a new response builder.
     * @throws java.lang.IllegalArgumentException
     *          if location is {@code null}.
     */
    public static ResponseBuilder seeOther(URI location) {
        return status(Status.SEE_OTHER).location(location);
    }

    /**
     * Create a new ResponseBuilder for a temporary redirection.
     *
     * @param location the redirection URI. If a relative URI is
     *                 supplied it will be converted into an absolute URI by resolving it
     *                 relative to the base URI of the application (see
     *                 {@link UriInfo#getBaseUri}).
     * @return a new response builder.
     * @throws java.lang.IllegalArgumentException
     *          if location is {@code null}.
     */
    public static ResponseBuilder temporaryRedirect(URI location) {
        return status(Status.TEMPORARY_REDIRECT).location(location);
    }

    /**
     * Create a new ResponseBuilder for a not acceptable response.
     *
     * @param variants list of variants that were available, a null value is
     *                 equivalent to an empty list.
     * @return a new response builder.
     */
    public static ResponseBuilder notAcceptable(List<Variant> variants) {
        return status(Status.NOT_ACCEPTABLE).variants(variants);
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
     * convenient methods to create such values as does {@code URI.create()}.</p>
     *
     * <p>Where multiple variants of the same method are provided, the type of
     * the supplied parameter is retained in the metadata of the built
     * {@code Response}.</p>
     */
    public static abstract class ResponseBuilder {

        /**
         * Protected constructor, use one of the static methods of
         * {@code Response} to obtain an instance.
         */
        protected ResponseBuilder() {
        }

        /**
         * Create a new builder instance.
         *
         * @return a new response builder.
         */
        protected static ResponseBuilder newInstance() {
            return RuntimeDelegate.getInstance().createResponseBuilder();
        }

        /**
         * Create a Response instance from the current ResponseBuilder. The builder
         * is reset to a blank state equivalent to calling the ok method.
         *
         * @return a Response instance.
         */
        public abstract Response build();

        /**
         * {@inheritDoc}
         * <p>
         * Create a copy of the ResponseBuilder preserving its state.
         * </p>
         *
         * @return a copy of the ResponseBuilder.
         */
        @Override
        @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
        public abstract ResponseBuilder clone();

        /**
         * Set the status on the ResponseBuilder.
         *
         * @param status the response status.
         * @return the updated response builder.
         * @throws IllegalArgumentException if status is less than {@code 100} or greater
         *                                  than {@code 599}.
         */
        public abstract ResponseBuilder status(int status);

        /**
         * Set the status on the ResponseBuilder.
         *
         * @param status the response status.
         * @return the updated response builder.
         * @throws IllegalArgumentException if status is {@code null}.
         * @since 1.1
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
         * @param status the response status.
         * @return the updated response builder.
         * @throws IllegalArgumentException if status is {@code null}.
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
         * @see #entity(java.lang.Object, java.lang.annotation.Annotation[])
         * @see #type(javax.ws.rs.core.MediaType)
         * @see #type(java.lang.String)
         */
        public abstract ResponseBuilder entity(Object entity);

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
         * @param entity      the request entity.
         * @param annotations annotations that will be passed to the {@link MessageBodyWriter}.
         * @return updated response builder instance.
         * @see #entity(java.lang.Object)
         * @see #type(javax.ws.rs.core.MediaType)
         * @see #type(java.lang.String)
         * @since 2.0
         */
        public abstract ResponseBuilder entity(Object entity, Annotation[] annotations);

        /**
         * Set the list of allowed methods for the resource. Any duplicate method
         * names will be truncated to a single entry.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *                if {@code null} any existing allowed method list will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder allow(String... methods);

        /**
         * Set the list of allowed methods for the resource.
         *
         * @param methods the methods to be listed as allowed for the resource,
         *                if {@code null} any existing allowed method list will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder allow(Set<String> methods);

        /**
         * Set the cache control data of the message.
         *
         * @param cacheControl the cache control directives, if {@code null}
         *                     any existing cache control directives will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder cacheControl(CacheControl cacheControl);

        /**
         * Set the message entity content encoding.
         *
         * @param encoding the content encoding of the message entity,
         *                 if {@code null} any existing value for content encoding will be
         *                 removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder encoding(String encoding);

        /**
         * Add an arbitrary header.
         *
         * @param name  the name of the header
         * @param value the value of the header, the header will be serialized
         *              using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if
         *              one is available via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
         *              for the class of {@code value} or using its {@code toString} method
         *              if a header delegate is not available. If {@code value} is {@code null}
         *              then all current headers of the same name will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder header(String name, Object value);

        /**
         * Replaces all existing headers with the newly supplied headers.
         *
         * @param headers new headers to be set, if {@code null} all existing
         *                headers will be removed.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *                 existing value for language will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder language(String language);

        /**
         * Set the message entity language.
         *
         * @param language the language of the message entity, if {@code null} any
         *                 existing value for type will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder language(Locale language);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *             existing value for type will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder type(MediaType type);

        /**
         * Set the message entity media type.
         *
         * @param type the media type of the message entity. If {@code null}, any
         *             existing value for type will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder type(String type);

        /**
         * Set message entity representation metadata.
         * <p/>
         * Equivalent to setting the values of content type, content language,
         * and content encoding separately using the values of the variant properties.
         *
         * @param variant metadata of the message entity, a {@code null} value is
         *                equivalent to a variant with all {@code null} properties.
         * @return the updated response builder.
         * @see #encoding(java.lang.String)
         * @see #language(java.util.Locale)
         * @see #type(javax.ws.rs.core.MediaType)
         * @since 2.0
         */
        public abstract ResponseBuilder variant(Variant variant);

        /**
         * Set the content location.
         *
         * @param location the content location. Relative or absolute URIs
         *                 may be used for the value of content location. If {@code null} any
         *                 existing value for content location will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder contentLocation(URI location);

        /**
         * Add cookies to the response message.
         *
         * @param cookies new cookies that will accompany the response. A {@code null}
         *                value will remove all cookies, including those added via the
         *                {@link #header(java.lang.String, java.lang.Object)} method.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder cookie(NewCookie... cookies);

        /**
         * Set the response expiration date.
         *
         * @param expires the expiration date, if {@code null} removes any existing
         *                expires value.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder expires(Date expires);

        /**
         * Set the response entity last modification date.
         *
         * @param lastModified the last modified date, if {@code null} any existing
         *                     last modified value will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder lastModified(Date lastModified);

        /**
         * Set the location.
         *
         * @param location the location. If a relative URI is supplied it will be
         *                 converted into an absolute URI by resolving it relative to the
         *                 base URI of the application (see {@link UriInfo#getBaseUri}).
         *                 If {@code null} any existing value for location will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder location(URI location);

        /**
         * Set a response entity tag.
         *
         * @param tag the entity tag, if {@code null} any existing entity tag
         *            value will be removed.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder tag(EntityTag tag);

        /**
         * Set a strong response entity tag.
         * <p/>
         * This is a shortcut for <code>tag(new EntityTag(<i>value</i>))</code>.
         *
         * @param tag the string content of a strong entity tag. The JAX-RS
         *            runtime will quote the supplied value when creating the header.
         *            If {@code null} any existing entity tag value will be removed.
         * @return the updated response builder.
         */
        @SuppressWarnings("HtmlTagCanBeJavadocTag")
        public abstract ResponseBuilder tag(String tag);

        /**
         * Add a Vary header that lists the available variants.
         *
         * @param variants a list of available representation variants, a {@code null}
         *                 value will remove an existing value for Vary header.
         * @return the updated response builder.
         * @since 2.0
         */
        public abstract ResponseBuilder variants(Variant... variants);

        /**
         * Add a Vary header that lists the available variants.
         *
         * @param variants a list of available representation variants, a {@code null}
         *                 value will remove an existing value for Vary header.
         * @return the updated response builder.
         */
        public abstract ResponseBuilder variants(List<Variant> variants);

        /**
         * Add one or more link headers.
         *
         * @param links links to be added to the message as headers, a {@code null}
         *              value will remove any existing Link headers.
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
         * Get the associated status code.
         *
         * @return the status code.
         */
        public int getStatusCode();

        /**
         * Get the class of status code.
         *
         * @return the class of status code.
         */
        public Status.Family getFamily();

        /**
         * Get the reason phrase.
         *
         * @return the reason phrase.
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
        REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
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
        private final Family family;

        /**
         * An enumeration representing the class of status code. Family is used
         * here since class is overloaded in Java.
         */
        public enum Family {

            /**
             * {@code 1xx} HTTP status codes.
             */
            INFORMATIONAL,
            /**
             * {@code 2xx} HTTP status codes.
             */
            SUCCESSFUL,
            /**
             * {@code 3xx} HTTP status codes.
             */
            REDIRECTION,
            /**
             * {@code 4xx} HTTP status codes.
             */
            CLIENT_ERROR,
            /**
             * {@code 5xx} HTTP status codes.
             */
            SERVER_ERROR,
            /**
             * Other, unrecognized HTTP status codes.
             */
            OTHER;

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
         * Get the class of status code.
         *
         * @return the class of status code.
         */
        @Override
        public Family getFamily() {
            return family;
        }

        /**
         * Get the associated status code.
         *
         * @return the status code.
         */
        @Override
        public int getStatusCode() {
            return code;
        }

        /**
         * Get the reason phrase.
         *
         * @return the reason phrase.
         */
        @Override
        public String getReasonPhrase() {
            return toString();
        }

        /**
         * Get the reason phrase.
         *
         * @return the reason phrase.
         */
        @Override
        public String toString() {
            return reason;
        }

        /**
         * Convert a numerical status code into the corresponding Status.
         *
         * @param statusCode the numerical status code.
         * @return the matching Status or null is no matching Status is defined.
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
