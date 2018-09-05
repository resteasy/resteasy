package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.plugins.providers.AbstractEntityProvider;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.WriterException;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A provider to handle multipart representations. This implementation will be
 * invoked when a method parameter takes a {@link MimeMultipart} as a method
 * parameter or a return value and the
 *
 * {@literal @}Consumes value is either multipart/mixed or multipart/form-data.
 * <p>
 * <code>
 * {@literal @}POST
 * {@literal @}Consumes("multipart/form-data") public void postData(MimeMultipart multipart) {
 * ...
 * </code>
 * <p>
 * When the {@link MimeMultipart} is passed to the method body, it is up to the
 * developer to extract the various parts.
 * 
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 */

@Provider
@Produces("multipart/mixed")
@Consumes({"multipart/mixed", "multipart/form-data"})
public class MimeMultipartProvider extends AbstractEntityProvider<MimeMultipart>
{

   /**
    * @param in input stream
    * @param mediaType media type
    * @return data source
    * @throws IOException if I/O error occurred
    */
   public static DataSource readDataSource(InputStream in, MediaType mediaType) throws IOException
   {
      ByteArrayDataSource ds = new ByteArrayDataSource(new BufferedInputStream(in), mediaType
              .toString());

      return ds;
   }

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
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return MimeMultipart.class.equals(type);
   }

   /**
    * Ascertain if the MessageBodyWriter supports a particular type.
    *
    * @param type        the class of instance that is to be written.
    * @param genericType the type of instance to be written, obtained either
    *                    by reflection of a resource method return type or via inspection
    *                    of the returned instance. {@link javax.ws.rs.core.GenericEntity}
    *                    provides a way to specify this information at runtime.
    * @param annotations an array of the annotations attached to the message entity instance.
    * @param mediaType   the media type of the HTTP entity.
    * @return {@code true} if the type is supported, otherwise {@code false}.
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return MimeMultipart.class.equals(type);
   }

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
    */
   public MimeMultipart readFrom(Class<MimeMultipart> type,
                                 Type genericType,
                                 Annotation[] annotations,
                                 MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders,
                                 InputStream entityStream) throws IOException
   {
      try
      {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
         DataSource ds = readDataSource(entityStream, mediaType);
         return new MimeMultipart(ds);
      }
      catch (MessagingException e)
      {
         throw new ReaderException(e);
      }
   }

   /**
    * Write a type to an HTTP message. The message header map is mutable
    * but any changes must be made before writing to the output stream since
    * the headers will be flushed prior to writing the message body.
    *
    * @param mimeMultipart the instance to write.
    * @param type         the class of instance that is to be written.
    * @param genericType  the type of instance to be written. {@link javax.ws.rs.core.GenericEntity}
    *                     provides a way to specify this information at runtime.
    * @param annotations  an array of the annotations attached to the message entity instance.
    * @param mediaType    the media type of the HTTP entity.
    * @param httpHeaders  a mutable map of the HTTP message headers.
    * @param entityStream the {@link OutputStream} for the HTTP entity. The
    *                     implementation should not close the output stream.
    * @throws java.io.IOException if an IO error arises.
    */
   public void writeTo(MimeMultipart mimeMultipart,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      try
      {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
         // replace the Content-Type header to include the boundry
         // information
         httpHeaders.putSingle("Content-Type", MediaType.valueOf(mimeMultipart.getContentType()));
         mimeMultipart.writeTo(entityStream);
      }
      catch (MessagingException e)
      {
         throw new WriterException(e);
      }

   }
}
