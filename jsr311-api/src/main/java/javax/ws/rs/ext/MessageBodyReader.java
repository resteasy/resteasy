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
 * MessageBodyReader.java
 *
 * Created on November 8, 2007, 3:57 PM
 *
 */

package javax.ws.rs.ext;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Contract for a provider that supports the conversion of a stream to a
 * Java type. To add a <code>MessageBodyReader</code> implementation, annotate the
 * implementation class with <code>@Provider</code>.
 *
 * A <code>MessageBodyReader</code> implementation may be annotated
 * with <code>@ConsumeMime</code> to restrict the media types for which it will
 * be considered suitable.
 *
 * @see Provider
 * @see javax.ws.rs.ConsumeMime
 */
@Contract
public interface MessageBodyReader<T> {

    /**
     * Ascertain if the MessageBodyReader supports a particular type.
     *
     * @param type the type that is to be supported.
     * @return true if the type is supported, otherwise false.
     */
    boolean isReadable(Class<?> type);

    /**
     * Read a type from the {@link InputStream}.
     *
     * @return the type that was read from the stream.
     * @param type the type that is to be read from the entity stream.
     *             May be null if only one type is supported.
     * @param mediaType the media type of the HTTP entity.
     * @param httpHeaders the HTTP headers associated with HTTP entity.
     * @param entityStream the {@link InputStream} of the HTTP entity.
     * @throws java.io.IOException if an IO error arises
     */
    T readFrom(Class<T> type, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException;

}