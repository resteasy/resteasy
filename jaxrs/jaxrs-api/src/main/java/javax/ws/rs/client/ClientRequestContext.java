/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.client;

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Client request filter context.
 *
 * A mutable class that provides request-specific information for the filter,
 * such as request URI, message headers, message entity or request-scoped
 * properties. The exposed setters allow modification of the exposed request-specific
 * information.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @since 2.0
 */
public interface ClientRequestContext {

    /**
     * Get a mutable map of request-scoped properties that can be used for communication
     * between different request/response processing components.
     *
     * May be empty, but MUST never be {@code null}. In the scope of a single
     * request/response processing a same property map instance is shared by the
     * following methods:
     * <ul>
     * <li>{@code ClientRequestContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.client.ClientResponseContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.ext.InterceptorContext#getProperties() }</li>
     * </ul>
     *
     * A request-scoped property is an application-defined property that may be
     * added, removed or modified by any of the components (user, filter,
     * interceptor etc.) that participate in a given request/response processing
     * flow.
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
     */
    public Map<String, Object> getProperties();

    /**
     * Get the request URI.
     *
     * @return request URI.
     */
    public URI getUri();

    /**
     * Set a new request URI.
     *
     * @param uri new request URI.
     */
    public void setUri(URI uri);

    /**
     * Get the request method.
     *
     * @return the request method.
     * @see javax.ws.rs.HttpMethod
     */
    public String getMethod();

    /**
     * Set the request method.
     *
     * @param method new request method.
     * @see javax.ws.rs.HttpMethod
     */
    public void setMethod(String method);

    /**
     * Get the mutable request headers multivalued map.
     *
     * @return mutable multivalued map of request headers.
     */
    public MultivaluedMap<String, Object> getHeaders();

    /**
     * Get message date.
     *
     * @return the message date, otherwise {@code null} if not present.
     */
    public Date getDate();

    /**
     * Get the language of the entity.
     *
     * @return the language of the entity or {@code null} if not specified
     */
    public Locale getLanguage();

    /**
     * Get the media type of the entity.
     *
     * @return the media type or {@code null} if not specified (e.g. there's no
     *         request entity).
     */
    public MediaType getMediaType();

    /**
     * Get a list of media types that are acceptable for the response.
     *
     * @return a read-only list of requested response media types sorted according
     *         to their q-value, with highest preference first.
     */
    public List<MediaType> getAcceptableMediaTypes();

    /**
     * Get a list of languages that are acceptable for the response.
     *
     * @return a read-only list of acceptable languages sorted according
     *         to their q-value, with highest preference first.
     */
    public List<Locale> getAcceptableLanguages();

    /**
     * Get any cookies that accompanied the request.
     *
     * @return a read-only map of cookie name (String) to {@link Cookie}.
     */
    public Map<String, Cookie> getCookies();

    /**
     * Check if there is an entity available in the request.
     *
     * The method returns {@code true} if the entity is present, returns
     * {@code false} otherwise.
     *
     * @return {@code true} if there is an entity present in the message,
     *         {@code false} otherwise.
     */
    public boolean hasEntity();

    /**
     * Get the message entity Java instance.
     *
     * Returns {@code null} if the message does not contain an entity.
     *
     * @return the message entity or {@code null} if message does not contain an
     *         entity body.
     */
    public Object getEntity();

    /**
     * Set a new response message entity.
     *
     * @param <T>         entity Java type.
     * @param type        declared entity class.
     * @param annotations annotations attached to the entity.
     * @param mediaType   entity media type.
     * @param entity      entity object.
     * @see MessageBodyWriter
     */
    public <T> void setEntity(
            final Class<T> type,
            final Annotation annotations[],
            final MediaType mediaType,
            final T entity);

    /**
     * Set a new response message entity.
     *
     * @param <T>         entity Java type.
     * @param type        declared generic entity type.
     * @param annotations annotations attached to the entity.
     * @param mediaType   entity media type.
     * @param entity      entity object.
     * @see MessageBodyWriter
     */
    public <T> void setEntity(
            final GenericType<T> type,
            final Annotation annotations[],
            final MediaType mediaType,
            final T entity);

    /**
     * Get the declared generic message entity type information.
     *
     * @return declared generic message entity type.
     */
    public GenericType<?> getDeclaredEntityType();

    /**
     * Get the annotations attached to the entity.
     *
     * @return entity annotations.
     */
    public Annotation[] getEntityAnnotations();


    /**
     * Get the entity output stream.
     *
     * @return entity output stream.
     */
    public OutputStream getEntityStream();

    /**
     * Set a new entity output stream.
     *
     * @param input new entity output stream.
     */
    public void setEntityStream(OutputStream input);

    /**
     * Get the client instance associated with the request.
     *
     * @return client instance associated with the request.
     */
    public Client getClient();

    /**
     * Get the configuration of the request.
     *
     * @return request configuration.
     */
    public Configuration getConfiguration();

    /**
     * Abort the filter chain with a response.
     *
     * This method breaks the filter chain processing and returns the provided
     * response back to the client. The provided response goes through the
     * chain of applicable response filters.
     *
     * @param response response to be sent back to the client.
     */
    public void abortWith(Response response);
}
