package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.core.messagebody.ReaderUtility;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GenericType;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class BaseClientResponse<T> extends ClientResponse<T>
{
   public static interface BaseClientResponseStreamFactory
   {
      InputStream getInputStream() throws IOException;

      void performReleaseConnection();
   }

   protected ResteasyProviderFactory providerFactory;
   protected String attributeExceptionsTo;
   protected MultivaluedMap<String, String> headers;
   protected String alternateMediaType;
   protected Class<?> returnType;
   protected Type genericReturnType;
   protected Annotation[] annotations = {};
   protected int status;
   protected boolean wasReleased = false;
   protected boolean streamWasRead = false;
   protected Object unmarshaledEntity;
   protected MessageBodyReaderInterceptor[] messageBodyReaderInterceptors;
   protected Exception exception;// These can only be set by an interceptor
   protected boolean cacheInputStream;
   protected BaseClientResponseStreamFactory streamFactory;

   public BaseClientResponse(BaseClientResponseStreamFactory streamFactory)
   {
      this.streamFactory = streamFactory;
   }

   public void setMessageBodyReaderInterceptors(MessageBodyReaderInterceptor[] messageBodyReaderInterceptors)
   {
      this.messageBodyReaderInterceptors = messageBodyReaderInterceptors;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.headers = headers;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setReturnType(Class<T> returnType)
   {
      this.returnType = returnType;
   }

   public Class<?> getReturnType()
   {
      return returnType;
   }

   public void setGenericReturnType(Type genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public String getAttributeExceptionsTo()
   {
      return attributeExceptionsTo;
   }

   public void setAttributeExceptionsTo(String attributeExceptionsTo)
   {
      this.attributeExceptionsTo = attributeExceptionsTo;
   }

   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public Annotation[] getAnnotations()
   {
      return this.annotations;
   }

   public String getResponseHeader(String headerKey)
   {
      if (headers == null) return null;
      return headers.getFirst(headerKey);
   }

   public void setAlternateMediaType(String alternateMediaType)
   {
      this.alternateMediaType = alternateMediaType;
   }

   public BaseClientResponseStreamFactory getStreamFactory()
   {
      return streamFactory;
   }

   public void setStreamFactory(BaseClientResponseStreamFactory streamFactory)
   {
      this.streamFactory = streamFactory;
   }

   @Override
   public T getEntity()
   {
      if (returnType == null)
      {
         throw new RuntimeException(
                 "No type information to extract entity with, use other getEntity() methods");
      }
      return (T) getEntity(returnType, genericReturnType, this.annotations);
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type)
   {
      return getEntity(type, null);
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type, Type genericType)
   {
      return getEntity(type, genericType, getAnnotations(type, genericType));
   }

   private <T2> Annotation[] getAnnotations(Class<T2> type, Type genericType)
   {
      return (this.returnType == type && this.genericReturnType == genericType) ? this.annotations
              : null;
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type, Type genericType, Annotation[] anns)
   {
      if (exception != null)
      {
         throw new RuntimeException("Unable to unmarshall response for "
                 + attributeExceptionsTo, exception);
      }

      if (unmarshaledEntity != null && !type.isInstance(this.unmarshaledEntity))
         throw new RuntimeException("The entity was already read, and it was of type "
                 + unmarshaledEntity.getClass());

      if (unmarshaledEntity == null)
      {
         try
         {
            if (status == HttpResponseCodes.SC_NO_CONTENT)
               return null;

            unmarshaledEntity = readFrom(type, genericType, getMediaType(), anns);
         }
         finally
         {
            if (!InputStream.class.isAssignableFrom(type))
               releaseConnection();
         }
      }
      return (T2) unmarshaledEntity;
   }

   protected MediaType getMediaType()
   {
      String mediaType = getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
      if (mediaType == null)
      {
         mediaType = alternateMediaType;
      }

      return mediaType == null ? MediaType.WILDCARD_TYPE : MediaType.valueOf(mediaType);
   }

   protected <T2> Object readFrom(Class<T2> type, Type genericType,
                                  MediaType media, Annotation[] annotations)
   {
      try
      {
         ReaderUtility reader = new ReaderUtility(providerFactory,
                 messageBodyReaderInterceptors)
         {
            @Override
            public RuntimeException createReaderNotFound(Type genericType,
                                                         MediaType mediaType)
            {
               return createResponseFailure(format(
                       "Unable to find a MessageBodyReader of content-type %s and type %s",
                       mediaType, genericType));
            }
         };
         return reader.doRead(type, genericType == null ? type : genericType,
                 media, this.annotations, getHeaders(), streamFactory
                         .getInputStream());
      }
      catch (IOException e)
      {
         String msg = "Failure reading from MessageBodyReader for conten-type: %s and type %s";
         throw createResponseFailure(format(msg, media.toString(), type.getName()), e);
      }
   }

   @Override
   public <T2> T2 getEntity(GenericType<T2> genericType)
   {
      return getEntity(genericType.getType(), genericType.getGenericType());
   }

   @Override
   public <T2> T2 getEntity(GenericType<T2> genericType, Annotation[] ann)
   {
      return getEntity(genericType.getType(), genericType.getGenericType(), ann);
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      // hack to cast from <String, String> to <String, Object>
      return (MultivaluedMap) headers;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   public void checkFailureStatus()
   {
      if (status > 399 && status < 599)
      {
         throw createResponseFailure(format("Error status %d %s returned", status, getResponseStatus()));
      }
   }

   public ClientResponseFailure createResponseFailure(String message)
   {
      return createResponseFailure(message, null);
   }

   public ClientResponseFailure createResponseFailure(String message, Exception e)
   {
      setException(e);
      this.returnType = byte[].class;
      this.genericReturnType = null;
      this.annotations = null;
      return new ClientResponseFailure(message, e, (ClientResponse<byte[]>) this);
   }

   @Override
   public Status getResponseStatus()
   {
      return Status.fromStatusCode(getStatus());
   }

   public boolean wasReleased()
   {
      return wasReleased;
   }

   public final void releaseConnection()
   {
      if (!wasReleased)
      {
         streamFactory.performReleaseConnection();
         wasReleased = true;
      }
   }

   @Override
   protected final void finalize() throws Throwable
   {
      releaseConnection();
   }

}
