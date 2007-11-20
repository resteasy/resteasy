/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * MessageBodyWriter.java
 *
 * Created on November 8, 2007, 3:57 PM
 *
 */

package javax.ws.rs.ext;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Contract for a provider that supports the conversion of a Java type to a
 * stream. To add a <code>MessageBodyWriter</code> implementation, annotate the
 * implementation class with <code>@Provider</code>.
 *
 * A <code>MessageBodyWriter</code> implementation may be annotated
 * with <code>@ProduceMime</code> to restrict the media types for which it will
 * be considered suitable.
 *
 * @see Provider
 * @see javax.ws.rs.ProduceMime
 */
@Contract
public interface MessageBodyWriter<T> {

    /**
     * Ascertain if the MessageBodyWriter supports a particular type.
     *
     * @param type the type that is to be supported.
     * @return true if the type is supported, otherwise false.
     */
    boolean isWriteable(Class<?> type);

    /**
     * Called before <code>writeTo</code> to ascertain the length in bytes of
     * the serialized form of <code>t</code>. A non-negative return value is
     * used in a HTTP <code>Content-Length</code> header.
     * @param t the type
     * @return length in bytes or -1 if the length cannot be determined in
     * advance
     */
    long getSize(T t);

    /**
     * Write a type to an HTTP response.
     *
     * @param t the type to write.
     * @param mediaType the media type of the HTTP entity.
     * @param httpHeaders the HTTP response headers.
     * @param entityStream the {@link OutputStream} for the HTTP entity.
     * @throws java.io.IOException if an IO error arises
     */
    void writeTo(T t, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            OutputStream entityStream) throws IOException;
}