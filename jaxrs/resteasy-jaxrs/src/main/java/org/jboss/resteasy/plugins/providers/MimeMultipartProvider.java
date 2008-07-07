package org.jboss.resteasy.plugins.providers;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
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
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @ConsumeMime value is either multipart/mixed or multipart/form-data.
 * <p/>
 * <code>
 * @POST
 * @ConsumeMime("multipart/form-data") 
 * public void postData(MimeMultipart multipart) {
 * ...
 * </code>
 * <p/>
 * When the {@link MimeMultipart} is passed to the method body, it is up to the
 * developer to extract the various parts.
 */

@Provider
@ProduceMime("multipart/mixed")
@ConsumeMime({"multipart/mixed", "multipart/form-data"})
public class MimeMultipartProvider extends AbstractEntityProvider<MimeMultipart>
{

   /**
    * @param type
    * @param genericType
    * @param annotations
    * @return
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return MimeMultipart.class.equals(type);
   }

   /**
    * @param type
    * @param genericType
    * @param annotations
    * @return
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return MimeMultipart.class.equals(type);
   }

   /**
    * @param t
    * @return
    */
   public long getSize(MimeMultipart mimeMultipart)
   {
      return -1;
   }

   /**
    * FIXME Comment this
    * 
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @return
    * @throws IOException
    * @throws WebApplicationException
    * @see @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
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
         DataSource ds = ProviderHelper.readDataSource(entityStream, mediaType);
         return new MimeMultipart(ds);
      }
      catch (MessagingException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * FIXME Comment this
    * 
    * @param mimeMultipart
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @throws IOException
    * @see @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
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
         // replace the Content-Type header to include the boundry
         // information
         httpHeaders.putSingle("Content-Type", MediaType.valueOf(mimeMultipart.getContentType()));
         mimeMultipart.writeTo(entityStream);
      }
      catch (MessagingException e)
      {
         throw new WebApplicationException(e);
      }

   }
}
