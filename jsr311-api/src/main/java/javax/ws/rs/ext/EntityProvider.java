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

package javax.ws.rs.ext;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;

/**
 * A provider that supports the conversion of a type to and from a stream.
 * To add a EntityProvider implementation, annotate the
 * implementation class with @Provider.
 *
 * @see Provider
 *
 * @author Paul.Sandoz@Sun.Com
 */
@Contract
public interface EntityProvider<T> {
    /**
     * Ascertain if the Provider supports a particular type.
     *
     * @param type the type that is to be supported.
     * @return true if the type is supported, otherwise false.
     */
    boolean supports(Class<?> type);

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
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException;    
}
