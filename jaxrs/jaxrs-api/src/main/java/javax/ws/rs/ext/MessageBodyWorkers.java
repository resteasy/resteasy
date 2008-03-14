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
 * MessageBodyWorkers.java
 * 
 * Created on March 5, 2008, 9:00 AM
 * 
 */

package javax.ws.rs.ext;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * An injectable interface providing lookup of {@link MessageBodyReader} and
 * {@link MessageBodyWriter} instances.
 *
 * @see javax.ws.rs.core.Context
 * @see MessageBodyReader
 * @see MessageBodyWriter
 */
public interface MessageBodyWorkers
{

   /**
    * Get a list of message body readers that match a set of criteria.
    *
    * @param mediaType   the media type of the data that will be read, this will
    *                    be compared to the values of {@link javax.ws.rs.ConsumeMime} for
    *                    each candidate reader and only matching readers will be included.
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
    * @return a list (possibly empty) of the MessageBodyReaders that match the
    *         supplied criteria.
    */
   public abstract <T> List<MessageBodyReader<T>> getMessageBodyReaders(MediaType mediaType, Class<T> type, Type genericType, Annotation annotations[]);


   /**
    * Get a list of message body writers that match a set of criteria.
    *
    * @param mediaType   the media type of the data that will be written, this will
    *                    be compared to the values of {@link javax.ws.rs.ProduceMime} for
    *                    each candidate writer and only matching writers will be included.
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
    * @return a list (possibly empty) of the MessageBodyReaders that match the
    *         supplied criteria.
    */
   public abstract <T> List<MessageBodyWriter<T>> getMessageBodyWriters(MediaType mediaType, Class<T> type, Type genericType, Annotation annotations[]);
}
