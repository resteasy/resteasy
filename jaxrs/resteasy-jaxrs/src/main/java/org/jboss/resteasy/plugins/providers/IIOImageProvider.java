/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.Provider;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 24,
 *         2008
 */
@Provider
@ConsumeMime("image/*")
@ProduceMime("image/*")
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
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations)
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
   public IIOImage readFrom(Class<IIOImage> type, Type genericType,
                            Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
           throws IOException, WebApplicationException
   {

      Iterator<ImageReader> readers = ImageIO
              .getImageReadersByMIMEType(mediaType.toString());

      ImageReader reader = null;
      while (readers.hasNext())
      {
         reader = (ImageReader) readers.next();
      }

      if (reader != null)
      {
         ImageInputStream iis = ImageIO.createImageInputStream(entityStream);
         reader.setInput(iis, false);
         IIOImage image = reader.readAll(0, null);
         return image;
      }
      else
      {
         String[] availableTypes = ImageIO.getReaderMIMETypes();
         List<Variant> variants = getAvailableVariants(availableTypes);
         Response response = Response.notAcceptable(variants)
                 .status(Status.NOT_ACCEPTABLE).build();
         throw new WebApplicationException(response);
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
   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations)
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
   public void writeTo(IIOImage t, Class<?> type, Type genericType,
                       Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException,
           WebApplicationException
   {
      Iterator<ImageWriter> writers = ImageIO
              .getImageWritersByMIMEType(mediaType.toString());
      ImageWriter writer = writers.next();
      if (writer == null)
      {
         Response response = Response.serverError().build();
         throw new WebApplicationException(response);
      }
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
      if (param.canWriteCompressed())
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
