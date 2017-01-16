/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
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
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class,
    *      java.lang.reflect.Type, java.lang.annotation.Annotation[])
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return IIOImage.class.equals(type);
   }

   /**
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @return
    * @throws IOException
    * @throws WebApplicationException
    * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class,
    *      java.lang.reflect.Type, java.lang.annotation.Annotation[],
    *      javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
    *      java.io.InputStream)
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
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class,
    *      java.lang.reflect.Type, java.lang.annotation.Annotation[])
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return IIOImage.class.equals(type);
   }

   /**
    * @param t
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @throws IOException
    * @throws WebApplicationException
    * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object,
    *      java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
    *      javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
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
            param.setCompressionMode(writerParams.compressionMode());
            param.setCompressionQuality(writerParams.compressionQuality());
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
