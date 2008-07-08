/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 20,
 *         2008
 */
@Provider
@ProduceMime("*/*")
@ConsumeMime("*/*")
public class DataContentProvider implements MessageBodyReader<Object>,
        MessageBodyWriter<Object>
{

   /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class,
   *      java.lang.reflect.Type, java.lang.annotation.Annotation[])
   */
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations)
   {
      // TODO Auto-generated method stub
      return true;
   }

   /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class,
   *      java.lang.reflect.Type, java.lang.annotation.Annotation[],
   *      javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
   *      java.io.InputStream)
   */
   public Object readFrom(Class type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap httpHeaders, InputStream entityStream)
           throws IOException, WebApplicationException
   {
      ByteArrayDataSource ds = new ByteArrayDataSource(
              new BufferedInputStream(entityStream), mediaType.toString());
      DataHandler dh = new DataHandler(ds);
      @SuppressWarnings("unused")
      DataFlavor df[] = dh.getTransferDataFlavors();
      //DataFlavor flava = findMatchingFlavor(type, df);
      return null;
   }

   /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object)
   */
   public long getSize(Object t)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class,
   *      java.lang.reflect.Type, java.lang.annotation.Annotation[])
   */
   public boolean isWriteable(Class type, Type genericType,
                              Annotation[] annotations)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object,
   *      java.lang.Class, java.lang.reflect.Type,
   *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
   *      javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
   */
   public void writeTo(Object t, Class type, Type genericType,
                       Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap httpHeaders, OutputStream entityStream)
           throws IOException, WebApplicationException
   {
      // TODO Auto-generated method stub
   }
}
