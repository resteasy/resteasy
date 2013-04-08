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
import java.util.Collection;

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
     * Returns the property with the given name registered in the current request/response
     * exchange context, or {@code null} if there is no property by that name.
     * <p>
     * A property allows a JAX-RS filters and interceptors to exchange
     * additional custom information not already provided by this interface.
     * </p>
     * <p>
     * A list of supported properties can be retrieved using {@link #getPropertyNames()}.
     * Custom property names should follow the same convention as package names.
     * </p>
     * <p>
     * In a Servlet container, on the server side, the properties are backed by the
     * {@code ServletRequest} and contain all the attributes available in the {@code ServletRequest}.
     * </p>
     *
     * @param name a {@code String} specifying the name of the property.
     * @return an {@code Object} containing the value of the property, or
     *         {@code null} if no property exists matching the given name.
     * @see #getPropertyNames()
     */
    public Object getProperty(String name);

    /**
     * Returns an immutable {@link java.util.Collection collection} containing the property
     * names available within the context of the current request/response exchange context.
     * <p>
     * Use the {@link #getProperty} method with a property name to get the value of
     * a property.
     * </p>
     * <p>
     * In a Servlet container, the properties are synchronized with the {@code ServletRequest}
     * and expose all the attributes available in the {@code ServletRequest}. Any modifications
     * of the properties are also reflected in the set of properties of the associated
     * {@code ServletRequest}.
     * </p>
     *
     * @return an immutable {@link java.util.Collection collection} of property names.
     * @see #getProperty
     */
    public Collection<String> getPropertyNames();

    /**
     * Binds an object to a given property name in the current request/response
     * exchange context. If the name specified is already used for a property,
     * this method will replace the value of the property with the new value.
     * <p>
     * A property allows a JAX-RS filters and interceptors to exchange
     * additional custom information not already provided by this interface.
     * </p>
     * <p>
     * A list of supported properties can be retrieved using {@link #getPropertyNames()}.
     * Custom property names should follow the same convention as package names.
     * </p>
     * <p>
     * If a {@code null} value is passed, the effect is the same as calling the
     * {@link #removeProperty(String)} method.
     * </p>
     * <p>
     * In a Servlet container, on the server side, the properties are backed by the
     * {@code ServletRequest} and contain all the attributes available in the {@code ServletRequest}.
     * </p>
     *
     * @param name   a {@code String} specifying the name of the property.
     * @param object an {@code Object} representing the property to be bound.
     */
    public void setProperty(String name, Object object);

    /**
     * Removes a property with the given name from the current request/response
     * exchange context. After removal, subsequent calls to {@link #getProperty}
     * to retrieve the property value will return {@code null}.
     * <p>
     * In a Servlet container, on the server side, the properties are backed by the
     * {@code ServletRequest} and contain all the attributes available in the {@code ServletRequest}.
     * </p>
     *
     * @param name a {@code String} specifying the name of the property to be removed.
     */
    public void removeProperty(String name);

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
