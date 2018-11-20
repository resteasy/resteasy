/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.util.FindAnnotation;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Consumes("image/*")
@Produces("image/*")
public class IIOImageProvider extends AbstractEntityProvider<IIOImage>
{
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
      return IIOImage.class.equals(type);
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
    * @throws javax.ws.rs.WebApplicationException
    *                             if a specific HTTP error response needs to be produced.
    *                             Only effective if thrown prior to the response being committed.
    */
   public IIOImage readFrom(Class<IIOImage> type,
                     Type genericType,
                     Annotation[] annotations,
                     MediaType mediaType,
                     MultivaluedMap<String, String> httpHeaders,
                     InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      ImageReader reader = IIOImageProviderHelper.getImageReaderByMediaType(mediaType);
      try
      {
         IIOImage image = IIOImageProviderHelper.readImage(entityStream, reader, 0);
         return image;
      }
      finally
      {
         reader.dispose();
      }

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
      return IIOImage.class.equals(type);
   }

   /**
    * Write a type to an HTTP message. The message header map is mutable
    * but any changes must be made before writing to the output stream since
    * the headers will be flushed prior to writing the message body.
    *
    * @param t            the instance to write.
    * @param type         the class of instance that is to be written.
    * @param genericType  the type of instance to be written. {@link javax.ws.rs.core.GenericEntity}
    *                     provides a way to specify this information at runtime.
    * @param annotations  an array of the annotations attached to the message entity instance.
    * @param mediaType    the media type of the HTTP entity.
    * @param httpHeaders  a mutable map of the HTTP message headers.
    * @param entityStream the {@link OutputStream} for the HTTP entity. The
    *                     implementation should not close the output stream.
    * @throws java.io.IOException if an IO error arises.
    * @throws javax.ws.rs.WebApplicationException
    *                             if a specific HTTP error response needs to be produced.
    *                             Only effective if thrown prior to the message being committed.
    */
   public void writeTo(IIOImage t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      ImageWriter writer = IIOImageProviderHelper.getImageWriterByMediaType(mediaType);
      ImageWriteParam param;
      if (mediaType.equals(MediaType.valueOf("image/jpeg")))
      {
         param = new JPEGImageWriteParam(Locale.US);
      }
      else
      {
         param = writer.getDefaultWriteParam();
      }

      /*
      * If the image output type supports compression, set it to the highest
      * maximum
      */
      ImageWriterParams writerParams = FindAnnotation.findAnnotation(annotations,
            ImageWriterParams.class);
      if (writerParams != null)
      {
         if (param.canWriteCompressed())
         {
            final int cm = writerParams.compressionMode();
            param.setCompressionMode(cm);
            if (ImageWriteParam.MODE_EXPLICIT == cm) {
               param.setCompressionQuality(writerParams.compressionQuality());
            }
         }
      }
      else if (param.canWriteCompressed())
      {
         param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
         param.setCompressionQuality(1.0f);
      }
      BufferedOutputStream buff = new BufferedOutputStream(entityStream, 2048);
      ImageOutputStream ios = ImageIO.createImageOutputStream(buff);
      try
      {
         writer.setOutput(ios);
         IIOImage img = new IIOImage(t.getRenderedImage(), null, null);
         writer.write(null, img, param);
      }
      finally
      {
         writer.dispose();
      }
   }

}
