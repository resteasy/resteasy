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
package javax.ws.rs.ext;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Contract for a provider that supports the conversion of a stream to a
 * Java type.
 *
 * A {@code MessageBodyReader} implementation may be annotated
 * with {@link javax.ws.rs.Consumes} to restrict the media types for which it will
 * be considered suitable.
 * <p>
 * Providers implementing {@code MessageBodyReader} contract must be either programmatically
 * registered in a JAX-RS runtime or must be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} annotation to be automatically discovered
 * by the JAX-RS runtime during a provider scanning phase.
 * </p>
 *
 * @param <T> Java type supported by the provider
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see Provider
 * @see javax.ws.rs.Consumes
 * @since 1.0
 */
public interface MessageBodyReader<T> {

    /**
     * Ascertain if the MessageBodyReader can produce an instance of a
     * particular type. The {@code type} parameter gives the
     * class of the instance that should be produced, the {@code genericType} parameter
     * gives the {@link java.lang.reflect.Type java.lang.reflect.Type} of the instance
     * that should be produced.
     * E.g. if the instance to be produced is {@code List<String>}, the {@code type} parameter
     * will be {@code java.util.List} and the {@code genericType} parameter will be
     * {@link java.lang.reflect.ParameterizedType java.lang.reflect.ParameterizedType}.
     *
     * @param type        the class of instance to be produced.
     * @param genericType the type of instance to be produced. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the formal type of the method parameter as returned by
     *                    {@code Method.getGenericParameterTypes}.
     * @param annotations an array of the annotations on the declaration of the
     *                    artifact that will be initialized with the produced instance. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the annotations on that parameter returned by
     *                    {@code Method.getParameterAnnotations}.
     * @param mediaType   the media type of the HTTP entity, if one is not
     *                    specified in the request then {@code application/octet-stream} is
     *                    used.
     * @return {@code true} if the type is supported, otherwise {@code false}.
     */
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType);

    /**
     * Read a type from the {@link InputStream}.
     * <p>
     * In case the entity input stream is empty, the reader is expected to either return a
     * Java representation of a zero-length entity or throw a {@link javax.ws.rs.core.NoContentException}
     * in case no zero-length entity representation is defined for the supported Java type.
     * A {@code NoContentException}, if thrown by a message body reader while reading a server
     * request entity, is automatically translated by JAX-RS server runtime into a {@link javax.ws.rs.BadRequestException}
     * wrapping the original {@code NoContentException} and rethrown for a standard processing by
     * the registered {@link javax.ws.rs.ext.ExceptionMapper exception mappers}.
     * </p>
     *
     * @param type         the type that is to be read from the entity stream.
     * @param genericType  the type of instance to be produced. E.g. if the
     *                     message body is to be converted into a method parameter, this will be
     *                     the formal type of the method parameter as returned by
     *                     {@code Method.getGenericParameterTypes}.
     * @param annotations  an array of the annotations on the declaration of the
     *                     artifact that will be initialized with the produced instance. E.g.
     *                     if the message body is to be converted into a method parameter, this
     *                     will be the annotations on that parameter returned by
     *                     {@code Method.getParameterAnnotations}.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  the read-only HTTP headers associated with HTTP entity.
     * @param entityStream the {@link InputStream} of the HTTP entity. The
     *                     caller is responsible for ensuring that the input stream ends when the
     *                     entity has been consumed. The implementation should not close the input
     *                     stream.
     * @return the type that was read from the stream. In case the entity input stream is empty, the reader
     *         is expected to either return an instance representing a zero-length entity or throw
     *         a {@link javax.ws.rs.core.NoContentException} in case no zero-length entity representation is
     *         defined for the supported Java type.
     * @throws java.io.IOException if an IO error arises. In case the entity input stream is empty
     *                             and the reader is not able to produce a Java representation for
     *                             a zero-length entity, {@code NoContentException} is expected to
     *                             be thrown.
     * @throws javax.ws.rs.WebApplicationException
     *                             if a specific HTTP error response needs to be produced.
     *                             Only effective if thrown prior to the response being committed.
     */
    public T readFrom(Class<T> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws java.io.IOException, javax.ws.rs.WebApplicationException;
}
