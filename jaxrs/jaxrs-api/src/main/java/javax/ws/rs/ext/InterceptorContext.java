/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Context shared by message body interceptors that can be used to wrap
 * calls to {@link javax.ws.rs.ext.MessageBodyReader#readFrom} and
 * {@link javax.ws.rs.ext.MessageBodyWriter#writeTo}. The getters and
 * setters in this context class correspond to the parameters in
 * the aforementioned methods.
 *
 * @author Santiago Pericas-Geertsen
 * @author Bill Burke
 * @see ReaderInterceptor
 * @see WriterInterceptor
 * @see ReaderInterceptorContext
 * @see WriterInterceptorContext
 * @since 2.0
 */
public interface InterceptorContext {

    /**
     * Get a mutable map of request-scoped properties that can be used for communication
     * between different request/response processing components.
     *
     * May be empty, but MUST never be {@code null}. In the scope of a single
     * request/response processing a same property map instance is shared by the
     * following methods:
     * <ul>
     * <li>{@link javax.ws.rs.container.ContainerRequestContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.container.ContainerResponseContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.client.ClientRequestContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.client.ClientResponseContext#getProperties() }</li>
     * <li>{@link javax.ws.rs.ext.InterceptorContext#getProperties() }</li>
     * </ul>
     * <p>
     * A request-scoped property is an application-defined property that may be
     * added, removed or modified by any of the components (user, filter,
     * interceptor etc.) that participate in a given request/response processing
     * flow.
     * </p>
     * <p>
     * On the client side, this property map is initialized by calling
     * {@link javax.ws.rs.client.Configuration#setProperties(java.util.Map) } or
     * {@link javax.ws.rs.client.Configuration#setProperty(java.lang.String, java.lang.Object) }
     * on the configuration object associated with the corresponding
     * {@link javax.ws.rs.client.Invocation request invocation}.
     * </p>
     * <p>
     * On the server side, specifying the initial values is implementation-specific.
     * </p>
     * <p>
     * If there are no initial properties set, the request-scoped property map is
     * initialized to an empty map.
     * </p>
     *
     * @return a mutable request-scoped property map.
     * @see javax.ws.rs.client.Configuration
     */
    public Map<String, Object> getProperties();

    /**
     * Get an array of the annotations formally declared on the artifact that
     * initiated the intercepted entity provider invocation.
     *
     * E.g. if the message body is to be converted into a method parameter, this
     * will be the annotations on that parameter returned by
     * {@link java.lang.reflect.Method#getParameterAnnotations Method.getParameterAnnotations()};
     * if the server-side response entity instance is to be converted into an
     * output stream, this will be the annotations on the matched resource method
     * returned by {@link java.lang.reflect.Method#getAnnotations() Method.getAnnotations()}.
     *
     * This method may return an empty array in case the interceptor is
     * not invoked in a context of any particular resource method
     * (e.g. as part of the client API), but will never return {@code null}.
     *
     * @return annotations declared on the artifact that initiated the intercepted
     *         entity provider invocation.
     */
    public Annotation[] getAnnotations();

    /**
     * Update annotations on the formal declaration of the artifact that
     * initiated the intercepted entity provider invocation.
     *
     * Calling this method has no effect in the client API.
     *
     * @param annotations updated annotations declarataion of the artifact that
     *                    initiated the intercepted entity provider invocation.
     *                    Must not be {@code null}.
     * @throws NullPointerException in case the input parameter is {@code null}.
     */
    public void setAnnotations(Annotation[] annotations);

    /**
     * Get Java type supported by corresponding message body provider.
     *
     * @return java type supported by provider
     */
    Class<?> getType();

    /**
     * Update Java type before calling message body provider.
     *
     * @param type java type for provider
     */
    public void setType(Class<?> type);

    /**
     * Get the type of the object to be produced or written.
     *
     * @return type of object produced or written
     */
    Type getGenericType();

    /**
     * Update type of the object to be produced or written.
     *
     * @param genericType new type for object
     */
    public void setGenericType(Type genericType);

    /**
     * Get media type of HTTP entity.
     *
     * @return media type of HTTP entity
     */
    public MediaType getMediaType();

    /**
     * Update media type of HTTP entity.
     *
     * @param mediaType new type for HTTP entity
     */
    public void setMediaType(MediaType mediaType);
}
