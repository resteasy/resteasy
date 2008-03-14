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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Contract for a provider that supports the conversion of a stream to a
 * Java type. To add a <code>MessageBodyReader</code> implementation, annotate the
 * implementation class with <code>@Provider</code>.
 * <p/>
 * A <code>MessageBodyReader</code> implementation may be annotated
 * with <code>@ConsumeMime</code> to restrict the media types for which it will
 * be considered suitable.
 *
 * @see Provider
 * @see javax.ws.rs.ConsumeMime
 */
public interface MessageBodyReader<T>
{

   /**
    * Ascertain if the MessageBodyReader can produce an instance of a particular type.
    *
    * @param type        the class of object to be produced.
    * @param genericType the type of object to be produced. E.g. if the
    *                    message body is to be converted into a method parameter, this will be
    *                    the formal type of the method parameter as returned by
    *                    <code>Class.getGenericParameterTypes</code>.
    * @param annotations an array of the annotations on the declaration of the
    *                    artifact that will be initialized with the produced instance. E.g. if the
    *                    message body is to be converted into a method parameter, this will be
    *                    the annotations on that parameter returned by
    *                    <code>Class.getParameterAnnotations</code>.
    * @return true if the type is supported, otherwise false.
    */
   boolean isReadable(Class<?> type, Type genericType, Annotation annotations[]);

   /**
    * Read a type from the {@link InputStream}.
    *
    * @param type         the type that is to be read from the entity stream.
    * @param genericType  the type of object to be produced. E.g. if the
    *                     message body is to be converted into a method parameter, this will be
    *                     the formal type of the method parameter as returned by
    *                     <code>Class.getGenericParameterTypes</code>.
    * @param annotations  an array of the annotations on the declaration of the
    *                     artifact that will be initialized with the produced instance. E.g. if the
    *                     message body is to be converted into a method parameter, this will be
    *                     the annotations on that parameter returned by
    *                     <code>Class.getParameterAnnotations</code>.
    * @param mediaType    the media type of the HTTP entity.
    * @param httpHeaders  the HTTP headers associated with HTTP entity.
    * @param entityStream the {@link InputStream} of the HTTP entity. The
    *                     implementation is not required to close the input stream but may do so
    *                     if desired.
    * @return the type that was read from the stream.
    * @throws java.io.IOException if an IO error arises
    */
   T readFrom(Class<T> type, Type genericType, MediaType mediaType,
              Annotation annotations[],
              MultivaluedMap<String, String> httpHeaders,
              InputStream entityStream) throws IOException;

}
