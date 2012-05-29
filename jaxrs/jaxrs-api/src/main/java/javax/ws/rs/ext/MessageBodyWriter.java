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
package javax.ws.rs.ext;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Contract for a provider that supports the conversion of a Java type to a
 * stream. To add a <code>MessageBodyWriter</code> implementation, annotate the
 * implementation class with <code>@Provider</code>.
 *
 * A <code>MessageBodyWriter</code> implementation may be annotated
 * with {@link javax.ws.rs.Produces} to restrict the media types for which it will
 * be considered suitable.
 *
 * @param <T> the type that can be written
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see Provider
 * @see javax.ws.rs.Produces
 * @since 1.0
 */
public interface MessageBodyWriter<T> {

    /**
     * Ascertain if the MessageBodyWriter supports a particular type.
     *
     * @param type the class of object that is to be written.
     * @param genericType the type of object to be written, obtained either
     * by reflection of a resource method return type or via inspection
     * of the returned instance. {@link javax.ws.rs.core.GenericEntity}
     * provides a way to specify this information at runtime.
     * @param annotations an array of the annotations on the resource
     * method that returns the object.
     * @param mediaType the media type of the HTTP entity.
     * @return true if the type is supported, otherwise false.
     */
    boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    /**
     * Called before <code>writeTo</code> to ascertain the length in bytes of
     * the serialized form of <code>t</code>. A non-negative return value is
     * used in a HTTP <code>Content-Length</code> header.
     * @param t the instance to write
     * @param type the class of object that is to be written.
     * @param genericType the type of object to be written, obtained either
     * by reflection of a resource method return type or by inspection
     * of the returned instance. {@link javax.ws.rs.core.GenericEntity}
     * provides a way to specify this information at runtime.
     * @param annotations an array of the annotations on the resource
     * method that returns the object.
     * @param mediaType the media type of the HTTP entity.
     * @return length in bytes or -1 if the length cannot be determined in
     * advance
     */
    long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType);

    /**
     * Write a type to an HTTP response. The response header map is mutable
     * but any changes must be made before writing to the output stream since
     * the headers will be flushed prior to writing the response body.
     *
     * @param t the instance to write.
     * @param type the class of object that is to be written.
     * @param genericType the type of object to be written, obtained either
     *     by reflection of a resource method return type or by inspection
     *     of the returned instance. {@link javax.ws.rs.core.GenericEntity}
     *     provides a way to specify this information at runtime.
     * @param annotations an array of the annotations on the resource
     *     method that returns the object.
     * @param mediaType the media type of the HTTP entity.
     * @param httpHeaders a mutable map of the HTTP response headers.
     * @param entityStream the {@link OutputStream} for the HTTP entity. The
     *     implementation should not close the output stream.
     * @throws java.io.IOException if an IO error arises
     * @throws javax.ws.rs.WebApplicationException if a specific
     *     HTTP error response needs to be produced. Only effective if thrown
     *     prior to the response being committed.
     */
    void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws java.io.IOException, javax.ws.rs.WebApplicationException;
}
