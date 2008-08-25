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
 * Providers.java
 *
 * Created on March 5, 2008, 9:00 AM
 *
 */

package javax.ws.rs.ext;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * An injectable interface providing runtime lookup of provider instances.
 *
 * @see javax.ws.rs.core.Context
 * @see MessageBodyReader
 * @see MessageBodyWriter
 * @see ContextResolver
 * @see ExceptionMapper
 */
public interface Providers
{

   /**
    * Get a message body reader that matches a set of criteria.
    *
    * @param mediaType   the media type of the data that will be read, this will
    *                    be compared to the values of {@link javax.ws.rs.Consumes} for
    *                    each candidate reader and only matching readers will be queried.
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
    * @return a MessageBodyReader that matches the supplied criteria or null
    *         if none is found.
    */
   <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation annotations[], MediaType mediaType);


   /**
    * Get a message body writer that matches a set of criteria.
    *
    * @param mediaType   the media type of the data that will be written, this will
    *                    be compared to the values of {@link javax.ws.rs.Produces} for
    *                    each candidate writer and only matching writers will be queried.
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
    * @return a MessageBodyReader that matches the supplied criteria or null
    *         if none is found.
    */
   <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation annotations[], MediaType mediaType);

   /**
    * Get an exception mapping provider for a particular class of exception.
    * Returns the provider whose generic type is the nearest superclass of
    * {@code type}.
    *
    * @param type the class of exception
    * @return an {@link ExceptionMapper} for the supplied type or null if none
    *         is found.
    */
   <T> ExceptionMapper<T> getExceptionMapper(Class<T> type);

   /**
    * Get a context resolver for a particular type of context for a class of
    * object. Returns a ContextResolver whose generic type is assignable to
    * {@code contextType} and that returns a non-null value from
    * {@code getContext(objectType)}.
    *
    * @param contextType the class of context desired
    * @param objectType  the class of object for which the context is desired
    * @param mediaType   the media type of data for which a context is required.
    *                    The value is compared to the values of {@link javax.ws.rs.Produces}
    *                    for each candidate and only matching providers will be considered.
    *                    A null value is equivalent to
    *                    {@link javax.ws.rs.core.MediaType#WILDCARD_TYPE}.
    * @return a matching context resolver or null if none is found.
    * @see ContextResolver#getContext(java.lang.Class)
    */
   <T> ContextResolver<T> getContextResolver(Class<T> contextType, Class<?> objectType, MediaType mediaType);
}