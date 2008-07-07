/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import javax.activation.DataSource;
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
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@ConsumeMime("*/*")
@ProduceMime("*/*")
public class DataSourceProvider extends AbstractEntityProvider<DataSource>
{

   
   /**
    * FIXME Comment this
    * 
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[])
    */
   public boolean isReadable(Class<?> type, 
                             Type genericType, 
                             Annotation[] annotations)
   {
      return DataSource.class.isAssignableFrom(type);
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
   public DataSource readFrom(Class<DataSource> type, 
                              Type genericType, 
                              Annotation[] annotations, 
                              MediaType mediaType,
                              MultivaluedMap<String, String> httpHeaders, 
                              InputStream entityStream) throws IOException
   {

      return ProviderHelper.readDataSource(entityStream, mediaType);
   }


   /**
    * FIXME Comment this
    * 
    * @param type
    * @param genericType
    * @param annotations
    * @return
    * @see @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[])
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return DataSource.class.isAssignableFrom(type);
   }

   /**
    * FIXME Comment this
    * 
    * @param dataSource
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @param entityStream
    * @throws IOException
    * @throws WebApplicationException
    * @see @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
    */
   public void writeTo(DataSource dataSource, 
                       Class<?> type, 
                       Type genericType, 
                       Annotation[] annotations,
                       MediaType mediaType, 
                       MultivaluedMap<String, Object> httpHeaders, 
                       OutputStream entityStream) throws IOException
   {
      InputStream in = dataSource.getInputStream();
      try
      {
         ProviderHelper.writeTo(in, entityStream);
      }
      finally
      {
         in.close();
      }

   }

}
