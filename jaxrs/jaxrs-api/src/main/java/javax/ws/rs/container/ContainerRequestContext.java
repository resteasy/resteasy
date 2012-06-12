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
package javax.ws.rs.container;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * TODO class javadoc.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @since 2.0
 */
public interface ContainerRequestContext {

    // mutable, shared with ContainerResponseContext
    Map<String, Object> getProperties();


    // request URI getters/setters
    public URI getBaseUri();

    public String getPath();

    public void setBaseUri(URI uri);

    public void setPath(String path);

    // request method getter and setter
    /**
     * Get the request method, e.g. GET, POST, etc.
     *
     * @return the request method.
     * @see javax.ws.rs.HttpMethod
     */
    public String getMethod();

    public void setMethod(String method);

    // mutable request headers map
    public MultivaluedMap<String, String> getHeaders();

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
     * Get Content-Length value.
     *
     * @return Content-Length as integer if present and valid number. In other
     *     cases returns {@code -1}.
     */
    public int getLength();

    /**
     * Get the media type of the entity.
     *
     * @return the media type or {@code null} if not specified (e.g. there's no
     *     request entity).
     */
    public MediaType getMediaType();

    /**
     * Get a list of media types that are acceptable for the response.
     *
     * @return a read-only list of requested response media types sorted according
     *     to their q-value, with highest preference first.
     */
    public List<MediaType> getAcceptableMediaTypes();

    /**
     * Get a list of languages that are acceptable for the response.
     *
     * @return a read-only list of acceptable languages sorted according
     *     to their q-value, with highest preference first.
     */
    public List<Locale> getAcceptableLanguages();

    /**
     * Get any cookies that accompanied the request.
     *
     * @return a read-only map of cookie name (String) to {@link Cookie}.
     */
    public Map<String, Cookie> getCookies();

    // returns true if the entity input stream is not empty, false otherwise.
    public boolean hasEntity();

    // invoking this method DOES NOT invoke handlers or MBR
    // returns empty stream if no entity
    public InputStream getEntityStream();

    // invoking this method DOES NOT invoke handlers or MBW
    public void setEntityStream(final InputStream entityStream);

    // conveniece method for setting new input stream; DOES invoke handlers & MBW
    // produced input stream is not buffered by default
    public <T> void writeEntity(
            final Class<T> type,
            final Annotation annotations[],
            final MediaType mediaType,
            final T entity);

       public <T> void writeEntity(
               final GenericType<T> genericType,
               final Annotation annotations[],
               final MediaType mediaType,
               final T entity);

    // consumes stream and invokes handlers & MBR to read entity
    // if entity is buffered, resets the buffering stream afterwards
    // (allows for multiple readEntity(...) calls on the same buffer})
    public <T> T readEntity(final Class<T> type) throws MessageProcessingException;

    public <T> T readEntity(final GenericType<T> entityType) throws MessageProcessingException;

    public <T> T readEntity(final Class<T> type, final Annotation[] annotations) throws MessageProcessingException;

    public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws MessageProcessingException;

    // buffers the entity input stream locally
    public void bufferEntity() throws MessageProcessingException;

    // SecurityContext getter/setter
    public SecurityContext getSecurityContext();

    public void setSecurityContext(SecurityContext context);

    public Request getRequest();

    // abort the request filter chain with a response
    public void abortWith(Response response);
}
