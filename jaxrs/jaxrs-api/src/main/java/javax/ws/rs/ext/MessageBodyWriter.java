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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Contract for a provider that supports the conversion of a Java type to a
 * stream. To add a <code>MessageBodyWriter</code> implementation, annotate the
 * implementation class with <code>@Provider</code>.
 * <p/>
 * A <code>MessageBodyWriter</code> implementation may be annotated
 * with <code>@ProduceMime</code> to restrict the media types for which it will
 * be considered suitable.
 *
 * @param T the type that can be written
 * @see Provider
 * @see javax.ws.rs.ProduceMime
 */
public interface MessageBodyWriter<T>
{

   /**
    * Ascertain if the MessageBodyWriter supports a particular type.
    *
    * @param type        the class of object that is to be written.
    * @param genericType the type of object to be written. E.g. if the
    *                    message body is to be produced from a field, this will be
    *                    the declared type of the field as returned by
    *                    <code>Field.getGenericType</code>.
    * @param annotations an array of the annotations on the declaration of the
    *                    artifact that will be written. E.g. if the
    *                    message body is to be produced from a field, this will be
    *                    the annotations on that field returned by
    *                    <code>Field.getDeclaredAnnotations</code>.
    * @return true if the type is supported, otherwise false.
    */
   boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[]);

   /**
    * Called before <code>writeTo</code> to ascertain the length in bytes of
    * the serialized form of <code>t</code>. A non-negative return value is
    * used in a HTTP <code>Content-Length</code> header.
    *
    * @param t the type
    * @return length in bytes or -1 if the length cannot be determined in
    *         advance
    */
   long getSize(T t);

   /**
    * Write a type to an HTTP response. The response header map is mutable
    * but any changes must be made before writing to the output stream since
    * the headers will be flushed prior to writing the response body.
    *
    * @param t            the instance to write.
    * @param type         the class of object that is to be written.
    * @param genericType  the type of object to be written. E.g. if the
    *                     message body is to be produced from a field, this will be
    *                     the declared type of the field as returned by
    *                     <code>Field.getGenericType</code>.
    * @param annotations  an array of the annotations on the declaration of the
    *                     artifact that will be written. E.g. if the
    *                     message body is to be produced from a field, this will be
    *                     the annotations on that field returned by
    *                     <code>Field.getDeclaredAnnotations</code>.
    * @param mediaType    the media type of the HTTP entity.
    * @param httpHeaders  a mutable map of the HTTP response headers.
    * @param entityStream the {@link OutputStream} for the HTTP entity. The
    *                     implementation is not required to close the input stream but may do so
    *                     if desired.
    * @throws java.io.IOException if an IO error arises
    */
   void writeTo(T t, Class<?> type, Type genericType, Annotation annotations[],
                MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders,
                OutputStream entityStream) throws IOException;
}
