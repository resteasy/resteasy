package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.interception.ClientReaderInterceptorContext;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ClientResponse extends BuiltResponse
{
   // One thing to note, I don't cache header objects because I was too lazy to proxy the headers multivalued map
   protected Map<String, Object> properties;
   protected ClientConfiguration configuration;
   protected byte[] bufferedEntity;

   protected ClientResponse(ClientConfiguration configuration)
   {
      setClientConfiguration(configuration);
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.metadata = new Headers<Object>();
      this.metadata.putAll(headers);
   }

   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public void setClientConfiguration(ClientConfiguration configuration)
   {
      this.configuration = configuration;
      this.processor = configuration;
   }

   @Override
   public Object getEntity()
   {
      abortIfClosed();
      return super.getEntity();
   }

   @Override
   public boolean hasEntity()
   {
      abortIfClosed();
      return entity != null || getMediaType() != null;
   }

   @Override
   public void close()
   {
      if (isClosed()) return;
      try {
         isClosed = true;
         releaseConnection();
      }
      catch (Exception e) {
         throw new ProcessingException(e);
      }
   }

   @Override
   protected void finalize() throws Throwable
   {
      if (isClosed()) return;
      try {
         close();
      }
      catch (Exception ignored) {
      }
   }

   @Override
   protected HeaderValueProcessor getHeaderValueProcessor()
   {
      return configuration;
   }

   protected abstract InputStream getInputStream();

   protected InputStream getEntityStream()
   {
      if (bufferedEntity != null) return new ByteArrayInputStream(bufferedEntity);
      if (isClosed()) throw new ProcessingException("Stream is closed");
      return getInputStream();
   }

   protected abstract void setInputStream(InputStream is);

   /**
    * release underlying connection but do not close
    *
    * @throws IOException
    */
   protected abstract void releaseConnection() throws IOException;


   public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns)
   {
      abortIfClosed();
      if (entity != null)
      {
         if (type.isInstance((this.entity)))
         {
            return (T)entity;
         }
         else if (entity instanceof InputStream)
         {
            setInputStream((InputStream)entity);
            entity = null;
         }
         else if (bufferedEntity == null)
         {
            throw new RuntimeException("The entity was already read, and it was of type "
                    + entity.getClass());
         }
         else
         {
            entity = null;
         }
      }

      if (entity == null)
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT)
            return null;

         try
         {
            entity = readFrom(type, genericType, getMediaType(), anns);
            if (entity == null || (entity != null
                    && !InputStream.class.isInstance(entity)
                    && !Reader.class.isInstance(entity)
                    && bufferedEntity == null)) close();
         }
         catch (RuntimeException e)
         {
            close();
            throw e;
         }
      }
      return (T) entity;
   }

   protected <T> Object readFrom(Class<T> type, Type genericType,
                                  MediaType media, Annotation[] annotations)
   {
      Type useGeneric = genericType == null ? type : genericType;
      Class<?> useType = type;
      media = media == null ? MediaType.WILDCARD_TYPE : media;
      annotations = annotations == null ? this.annotations : annotations;
      boolean isMarshalledEntity = false;
      if (type.equals(MarshalledEntity.class))
      {
         isMarshalledEntity = true;
         ParameterizedType param = (ParameterizedType) useGeneric;
         useGeneric = param.getActualTypeArguments()[0];
         useType = Types.getRawType(useGeneric);
      }


      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, configuration);
      try
      {
         InputStream is = getEntityStream();
         if (is == null)
         {
            throw new IllegalStateException("Input stream was empty, there is no entity");
         }
         if (isMarshalledEntity)
         {
            is = new InputStreamToByteArray(is);

         }

         ReaderInterceptor[] readerInterceptors = configuration.getReaderInterceptors(null, null);

         final Object obj = new ClientReaderInterceptorContext(readerInterceptors, configuration.getProviderFactory(), useType,
                 useGeneric, annotations, media, getStringHeaders(), is, properties)
                 .proceed();
         if (isMarshalledEntity)
         {
            InputStreamToByteArray isba = (InputStreamToByteArray) is;
            final byte[] bytes = isba.toByteArray();
            return new MarshalledEntity()
            {
               @Override
               public byte[] getMarshalledBytes()
               {
                  return bytes;
               }

               @Override
               public Object getEntity()
               {
                  return obj;
               }
            };
         }
         else
         {
            return (T) obj;
         }

      }
      catch (ProcessingException pe)
      {
         throw pe;
      }
      catch (Exception ex)
      {
         throw new ProcessingException(ex);
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);

      }
   }

   @Override
   public boolean bufferEntity()
   {
      abortIfClosed();
      if (bufferedEntity != null) return true;
      if (entity != null) return false;
      if (metadata.getFirst(HttpHeaderNames.CONTENT_TYPE) == null) return false;
      try
      {
         bufferedEntity = ReadFromStream.readFromStream(1024, getInputStream());
      }
      catch (IOException e)
      {
         throw new ProcessingException(e);
      }
      finally
      {
         try {
            releaseConnection();
         }
         catch (IOException e) {
            throw new ProcessingException(e);
         }
      }
      return true;
   }

}
